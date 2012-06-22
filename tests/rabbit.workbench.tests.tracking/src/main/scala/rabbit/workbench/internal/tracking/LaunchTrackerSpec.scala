/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package rabbit.workbench.internal.tracking

import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep
import java.util.Collections.emptySet
import java.util.concurrent.{ CountDownLatch, ConcurrentHashMap, ConcurrentMap }

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.mapAsJavaMap

import org.eclipse.core.resources.ResourcesPlugin.getWorkspace
import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.{ Path, IPath }
import org.eclipse.debug.core.DebugEvent.{ TERMINATE, SUSPEND, CREATE }
import org.eclipse.debug.core.ILaunchManager.{ RUN_MODE, DEBUG_MODE }
import org.eclipse.debug.core.model.{ ISuspendResume, IProcess }
import org.eclipse.debug.core.{ ILaunchConfigurationType, ILaunchConfiguration, ILaunch, IDebugEventSetListener, DebugPlugin, DebugEvent }
import org.eclipse.jdt.core.JavaCore.newContainerEntry
import org.eclipse.jdt.core.{ JavaCore, IPackageFragment, ICompilationUnit }
import org.eclipse.jdt.debug.core.JDIDebugModel
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.{ ID_JAVA_APPLICATION, ATTR_PROJECT_NAME, ATTR_MAIN_TYPE_NAME }
import org.eclipse.jdt.launching.JavaRuntime.{ getVMInstallTypes, JRE_CONTAINER }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Matchers
import org.mockito.Mockito.{ verifyZeroInteractions, verify, never, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.concurrent.Timeouts
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import com.google.common.collect.Sets

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.{ IEventListener, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class LaunchTrackerSpec extends AbstractTrackerSpecBase with Timeouts {

  /*
   * A debug listener uses a CountDownLatch to notify creation and termination
   * (total of 2 count downs) of processes.
   */
  private class DebugListener(latch: CountDownLatch) extends IDebugEventSetListener {

    var creationTimes: ConcurrentMap[ILaunch, Long] = new ConcurrentHashMap
    var terminationTimes: ConcurrentMap[ILaunch, Long] = new ConcurrentHashMap

    override def handleDebugEvents(events: Array[DebugEvent]) {
      events filter (_.getSource.isInstanceOf[IProcess]) foreach { e =>
        val launch = e.getSource.asInstanceOf[IProcess].getLaunch
        e.getKind match {
          case CREATE => {
            creationTimes.put(launch, currentTimeMillis)
            if (latch != null) latch.countDown
          }
          case TERMINATE => {
            terminationTimes.put(launch, currentTimeMillis)
            if (latch != null) latch.countDown
          }
          case _ =>
        }
      }
    }
  }

  private class Expected {
    var files = emptySet[IPath]
    var launch: ILaunch = _
    var launchConfig: ILaunchConfiguration = _
    var launchConfigType: ILaunchConfigurationType = _
    var start = 0L
    var end = 0L
    var duration = 0L
  }

  private var project: IProject = _
  private var pkg: IPackageFragment = _

  private var configsToDelete: Seq[ILaunchConfiguration] = _

  private var plugin: DebugPlugin = _
  private var listener: IEventListener[ILaunchEvent] = _
  @volatile private var captured: Seq[ILaunchEvent] = _

  override def beforeEach {
    plugin = DebugPlugin.getDefault
    configsToDelete = Seq.empty

    listener = mock[IEventListener[ILaunchEvent]]
    captured = Seq.empty
    doAnswer { i: InvocationOnMock =>
      captured = captured :+ i.getArguments()(0).asInstanceOf[ILaunchEvent]
    } when listener onEvent any[ILaunchEvent]

    // Create a new project
    project = getWorkspace.getRoot.getProject("p")
    project.create(null)
    project.open(null)

    // Add Java nature
    val desc = project.getDescription
    desc.setNatureIds(desc.getNatureIds :+ JavaCore.NATURE_ID)
    project.setDescription(desc, null)

    // Add VM path
    val java = JavaCore.create(project)
    getVMInstallTypes.flatMap(_.getVMInstalls).headOption match {
      case Some(install) => {
        val entry = newContainerEntry(new Path(JRE_CONTAINER)
          .append(install.getVMInstallType.getId)
          .append(install.getName))
        java.setRawClasspath((java.getRawClasspath.toSet + entry).toArray, null)
      }
      case None => throw new AssertionError("Can't find a VM install")
    }

    // Create a package
    val src = java.getPackageFragmentRoots()(0)
    pkg = src.createPackageFragment("pkg", true, null)

    super.beforeEach
  }

  override def afterEach {
    super.afterEach

    configsToDelete.foreach(_.delete)
    project.delete(true, true, null)
  }

  behavior of classOf[LaunchTracker].getSimpleName

  it must "not track launch events if stopped" in {
    tracker.start
    tracker.stop

    val code = codeToSleep(0, "Test")
    launch(pkg.createCompilationUnit("Test.java", code, true, null))

    verifyZeroInteractions(listener)
  }

  it must "track normal launches" in {
    tracker.start

    val duration = 100L
    val latch = new CountDownLatch(2)
    plugin.addDebugEventListener(new DebugListener(latch))
    val code = codeToSleep(duration, "Test")
    val unit = pkg.createCompilationUnit("Test.java", code, true, null)

    val expected = new Expected
    expected.duration = duration
    expected.start = currentTimeMillis
    expected.launch = launch(unit, RUN_MODE)
    expected.launchConfig = expected.launch.getLaunchConfiguration
    expected.launchConfigType = expected.launchConfig.getType
    latch.await
    waitForCapture
    expected.end = currentTimeMillis

    verifyEvent(captured(0), expected)
  }

  it must "track debug launches" in {
    tracker.start

    val latch = new CountDownLatch(2)
    plugin.addDebugEventListener(new DebugListener(latch))

    val code = codeToSleep(0, "Test");
    launch(pkg.createCompilationUnit("Test.java", code, true, null), DEBUG_MODE)
    latch.await
    waitForCapture

    captured(0) must not be null
  }

  it must "track multiple launches" in {
    tracker.start

    val latch = new CountDownLatch(2 * 2)
    val debug = new DebugListener(latch)
    plugin.addDebugEventListener(debug)

    val expected1 = new Expected
    val expected2 = new Expected

    expected1.duration = 1500
    expected2.duration = 1000

    val src1 = codeToSleep(expected1.duration, "Test1")
    val src2 = codeToSleep(expected2.duration, "Test2")
    expected1.launch = launch(pkg.createCompilationUnit("Test1.java", src1, true, null))
    expected2.launch = launch(pkg.createCompilationUnit("Test2.java", src2, true, null))

    expected1.launchConfig = expected1.launch.getLaunchConfiguration
    expected1.launchConfigType = expected1.launchConfig.getType

    expected2.launchConfig = expected2.launch.getLaunchConfiguration
    expected2.launchConfigType = expected2.launchConfig.getType

    latch.await
    failAfter(5 seconds) {
      while (captured.isEmpty) {
        sleep(100)
      }
    }

    expected1.start = debug.creationTimes.get(expected1.launch) - 10
    expected1.end = debug.terminationTimes.get(expected1.launch) + 10

    expected2.start = debug.creationTimes.get(expected2.launch) - 10
    expected2.end = debug.terminationTimes.get(expected2.launch) + 10

    captured = captured.sortBy(_.launchConfig.getName)
    captured.size must be(2)
    verifyEvent(captured(0), expected1)
    verifyEvent(captured(1), expected2)
  }

  it must "track files on breakpoint" in {
    tracker.start

    val src = "package " + pkg.getElementName + ";" +
      "\n public class Test {" +
      "\n   public static void main(String[] args) {" +
      "\n     System.out.println();" + // Line 4
      "\n   }" +
      "\n }"

    val unit = pkg.createCompilationUnit("Test.java", src, true, null)

    // Create a breakpoint on line 4
    JDIDebugModel.createLineBreakpoint(
      unit.getResource, // resource
      unit.getType("Test").getFullyQualifiedName, // typeName 
      4, // lineNumber
      -1, // charStart
      -1, // charEnd
      0, // hitCount
      true, // register
      null) // attributes

    // Find the ISuspendResume when stopped on breakpoint
    var suspend: ISuspendResume = null
    val latch = new CountDownLatch(1)
    val debug = new DebugListener(null) {
      override def handleDebugEvents(events: Array[DebugEvent]) {
        super.handleDebugEvents(events)
        events find (_.getKind == SUSPEND) foreach { e =>
          suspend = e.getSource.asInstanceOf[ISuspendResume]
          latch.countDown
        }
      }
    }
    DebugPlugin.getDefault.addDebugEventListener(debug)

    // Launch in debug mode so the breakpoint will be hit
    launch(unit, DEBUG_MODE)
    latch.await // Wait until we get the suspend event
    suspend.resume // Resume to let the launch finish

    waitForCapture

    // Should have the source file captured
    captured(0) must not be null
    captured(0).files must be(Sets.newHashSet(unit.getResource.getFullPath))
  }

  it must "add listener to debug plugin on start" in {
    val plugin = mock[DebugPlugin]
    val tracker = create(plugin, listener)
    try {
      tracker.start
      verify(plugin).addDebugEventListener(notNull[IDebugEventSetListener])
      verify(plugin, never).removeDebugEventListener(notNull[IDebugEventSetListener])
    } finally {
      tracker.stop
    }
  }

  it must "remove listener from debug plugin on stop" in {
    val plugin = mock[DebugPlugin]
    val tracker = create(plugin, listener)
    try {
      tracker.start
      tracker.stop
      verify(plugin).removeDebugEventListener(notNull[IDebugEventSetListener])
    } finally {
      tracker.stop
    }
  }

  override protected type Tracker = LaunchTracker

  override protected def create() = create(plugin, listener)

  private def create(plugin: DebugPlugin, listener: IEventListener[ILaunchEvent]) =
    new LaunchTracker(plugin, listener)

  private def launch(unit: ICompilationUnit): ILaunch = launch(unit, RUN_MODE)

  private def launch(unit: ICompilationUnit, mode: String) = {
    val clazz = unit.getTypes()(0)
    val manager = DebugPlugin.getDefault.getLaunchManager
    val app = manager.getLaunchConfigurationType(ID_JAVA_APPLICATION).newInstance(null, clazz.getElementName)
    app.setAttributes(Map(
      ATTR_PROJECT_NAME -> unit.getResource.getProject.getName,
      ATTR_MAIN_TYPE_NAME -> clazz.getFullyQualifiedName))
    val launch = app.doSave
    configsToDelete :+= launch
    launch.launch(mode, null, true)
  }

  private def codeToSleep(duration: Long, clazz: String) =
    "\n package " + pkg.getElementName + ";" +
      "\n public class " + clazz + " {" +
      "\n   public static void main(String[] args) throws Exception {" +
      "\n     Thread.sleep(" + duration + ");" +
      "\n   }" +
      "\n }"

  private def verifyEvent(event: ILaunchEvent, expected: Expected) {
    event must not be null
    event.launch must be(expected.launch)
    event.launchConfig must be(expected.launchConfig)
    event.launchConfigType must be(expected.launchConfigType)
    event.files must be(expected.files)
    event.duration.getMillis must be >= expected.duration
    event.instant.getMillis must be >= expected.start
    (event.instant.getMillis + event.duration.getMillis) must be <= expected.end
  }

  private def waitForCapture() = failAfter(5 seconds) {
    while (captured.isEmpty) {
      sleep(100)
    }
  }

  private def notNull[T]: T = Matchers.notNull.asInstanceOf[T]
}