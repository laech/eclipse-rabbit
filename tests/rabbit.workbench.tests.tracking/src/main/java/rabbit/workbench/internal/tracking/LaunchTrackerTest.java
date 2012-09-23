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

package rabbit.workbench.internal.tracking;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Thread.sleep;
import static java.util.Arrays.copyOf;
import static java.util.Collections.emptySet;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.debug.core.DebugEvent.SUSPEND;
import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.eclipse.jdt.core.JavaCore.newContainerEntry;
import static org.eclipse.jdt.debug.core.JDIDebugModel.createLineBreakpoint;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;
import static org.eclipse.jdt.launching.JavaRuntime.JRE_CONTAINER;
import static org.eclipse.jdt.launching.JavaRuntime.getVMInstallTypes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static rabbit.tracking.tests.Instants.epoch;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import rabbit.tracking.AbstractTrackerTestBase;
import rabbit.tracking.util.IClock;
import rabbit.workbench.tracking.event.LaunchEvent;

import com.google.common.collect.Iterators;
import com.google.common.eventbus.EventBus;

public final class LaunchTrackerTest
    extends AbstractTrackerTestBase<LaunchTracker> {

  private IJavaProject project;
  private IPackageFragment pkg;
  private List<ILaunchConfiguration> configsToDelete;

  private EventBus bus;
  private IClock clock;
  private DebugPlugin plugin;

  @Override public void after() throws Exception {
    super.after();
    for (ILaunchConfiguration config : configsToDelete)
      config.delete();

    project.getProject().delete(true, true, null);
  }

  @Test public void doesntTrackWhenStopped() throws Exception {
    tracker().start();
    tracker().stop();
    runGeneratedCode();
    verifyZeroInteractions(bus);
  }

  @Test public void registersListenerToDebugPluginOnStart() {
    DebugPlugin plugin = mock(DebugPlugin.class);
    LaunchTracker tracker = newTracker(plugin);
    try {
      tracker.start();
      verify(plugin).addDebugEventListener(anyDebugListener());
      verify(plugin, never()).removeDebugEventListener(anyDebugListener());
    } finally {
      tracker.stop();
    }
  }

  @Test public void removesListenerFromDebugPluginOnStop() {
    DebugPlugin plugin = mock(DebugPlugin.class);
    LaunchTracker tracker = newTracker(plugin);
    try {
      tracker.start();
      tracker.stop();
      verify(plugin).removeDebugEventListener(anyDebugListener());
    } finally {
      tracker.stop();
    }
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutClock() {
    newTracker(bus, null, plugin);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutDebugPlugin() {
    newTracker(bus, clock, null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutEventBus() {
    newTracker(null, clock, plugin);
  }

  @Test(timeout = 5000)//
  public void tracksDebugLaunch() throws Exception {
    tracker().start();
    debugGeneratedCode();
    assertThat(capturedEvent(), is(notNullValue()));
  }

  @Test(timeout = 5000)//
  public void tracksFilesOnBreakpointHit() throws Exception {
    tracker().start();

    ICompilationUnit unit = createUnitWithBreakpoint();
    ISuspendResume[] suspend = {null};
    CountDownLatch latch = new CountDownLatch(1);
    DebugPlugin.getDefault().addDebugEventListener(
        countDownOnSuspend(latch, suspend));

    debug(unit);
    latch.await();
    suspend[0].resume();

    assertThat(capturedEvent().files(), is(fileToSet(unit)));
  }

  @Test(timeout = 10000)//
  public void tracksMultipleLaunches() throws Exception {
    tracker().start();

    configClockToReturnInSequence(
        instant(0), // Start for #1
        instant(1), // Start for #2
        instant(10), // End for #1
        instant(91)); // End for #2

    ILaunch launch1 = run(generatedSleepingCode("Test1", 2000));
    sleep(200);
    ILaunch launch2 = run(generatedSleepingCode("Test2", 2000));

    assertThat(capturedEvents(2), hasItems(
        event(instant(0), durationBetween(0, 10), launch1, noFiles()),
        event(instant(1), durationBetween(1, 91), launch2, noFiles())));
  }

  @Test(timeout = 5000)//
  public void tracksNormalLaunch() throws Exception {
    tracker().start();

    configClockToReturnInSequence(instant(0), instant(100));
    ILaunch launch = runGeneratedCode();

    assertThat(capturedEvent(),
        is(event(instant(0), durationBetween(0, 100), launch, noFiles())));
  }

  @Override protected void init() throws Exception {
    super.init();

    configsToDelete = newArrayList();
    project = createJavaProject("project1");
    pkg = createPackage("pkg");

    bus = mock(EventBus.class);
    plugin = DebugPlugin.getDefault();
    clock = mock(IClock.class);
    given(clock.now()).willReturn(epoch());
  }

  @Override protected LaunchTracker newTracker() {
    return newTracker(plugin);
  }

  private IProject addJavaNature(IProject project) throws CoreException {
    IProjectDescription desc = project.getDescription();
    desc.setNatureIds(concat(desc.getNatureIds(), JavaCore.NATURE_ID));
    project.setDescription(desc, null);
    return project;
  }

  private IDebugEventSetListener anyDebugListener() {
    return any(IDebugEventSetListener.class);
  }

  private LaunchEvent capturedEvent() throws InterruptedException {
    return capturedEvents(1).get(0);
  }

  private List<LaunchEvent> capturedEvents(int n) throws InterruptedException {
    ArgumentCaptor<LaunchEvent> args = forClass(LaunchEvent.class);
    verifyOnPost(bus, times(n)).post(args.capture());
    return newArrayList(args.getAllValues());
  }

  private String code(String clazz) {
    return sleepingCode(clazz, 0);
  }

  private <T> T[] concat(T[] src, T elem) {
    T[] result = copyOf(src, src.length + 1);
    result[src.length] = elem;
    return result;
  }

  private void configClockToReturnInSequence(Instant... instants) {
    final Iterator<Instant> it = Iterators.forArray(instants);
    given(clock.now()).willAnswer(new Answer<Instant>() {
      @Override public Instant answer(InvocationOnMock invocation) {
        return it.next();
      }
    });
  }

  private IDebugEventSetListener countDownOnSuspend(
      final CountDownLatch latch, final ISuspendResume[] suspend) {
    return new IDebugEventSetListener() {
      @Override public void handleDebugEvents(DebugEvent[] events) {
        for (DebugEvent event : events) {
          if (event.getKind() == SUSPEND) {
            suspend[0] = (ISuspendResume)event.getSource();
            latch.countDown();
            break;
          }
        }
      }
    };
  }

  private ILaunchConfiguration createJavaLaunchConfig(ICompilationUnit unit)
      throws CoreException {
    IType type = unit.getTypes()[0];
    String project = unit.getResource().getProject().getName();

    ILaunchConfigurationWorkingCopy app = createJavaLaunchWorkingCopy(type);
    app.setAttribute(ATTR_PROJECT_NAME, project);
    app.setAttribute(ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
    ILaunchConfiguration config = app.doSave();

    return config;
  }

  private ILaunchConfigurationWorkingCopy createJavaLaunchWorkingCopy(IType type)
      throws CoreException {
    return DebugPlugin.getDefault()
        .getLaunchManager()
        .getLaunchConfigurationType(ID_JAVA_APPLICATION)
        .newInstance(null, type.getElementName());
  }

  private IJavaProject createJavaProject(String name) throws CoreException {
    IJavaProject project = JavaCore.create(addJavaNature(createProject(name)));
    IClasspathEntry entry = newEntry(findVm());
    project.setRawClasspath(concat(project.getRawClasspath(), entry), null);
    return project;
  }

  private IPackageFragment createPackage(String name) throws JavaModelException {
    IPackageFragmentRoot src = project.getPackageFragmentRoots()[0];
    return src.createPackageFragment(name, true, null);
  }

  private IProject createProject(String name) throws CoreException {
    IProject project = getWorkspace().getRoot().getProject(name);
    project.create(null);
    project.open(null);
    return project;
  }

  private ICompilationUnit createUnit(String name, String code)
      throws JavaModelException {
    return pkg.createCompilationUnit(name, code, true, null);
  }

  private ICompilationUnit createUnitWithBreakpoint() throws CoreException {
    String code = "package " + pkg.getElementName() + ";" +
        "\n public class Test {" +
        "\n   public static void main(String[] args) {" +
        "\n     System.out.println();" + // Line 4
        "\n   }" +
        "\n }";

    ICompilationUnit unit = createUnit("Test.java", code);
    putBreakpointAtLine(4, unit);
    return unit;
  }

  private ILaunch debug(ICompilationUnit unit) throws CoreException {
    return launch(unit, DEBUG_MODE);
  }

  private ILaunch debugGeneratedCode() throws CoreException, JavaModelException {
    return debug(generatedCode());
  }

  private Duration durationBetween(long start, long end) {
    return new Duration(start, end);
  }

  private LaunchEvent event(
      Instant instant,
      Duration duration,
      ILaunch launch,
      Set<IPath> files) throws CoreException {
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    ILaunchConfigurationType type = config.getType();
    return new LaunchEvent(instant, duration, launch, config, type, files);
  }

  private Set<IPath> fileToSet(ICompilationUnit unit) {
    return newHashSet(unit.getResource().getFullPath());
  }

  private IVMInstall findVm() {
    for (IVMInstallType type : getVMInstallTypes()) {
      IVMInstall[] vms = type.getVMInstalls();
      if (vms.length > 0)
        return vms[0];
    }
    throw new AssertionError("No VM found");
  }

  private ICompilationUnit generatedCode() throws JavaModelException {
    return pkg.createCompilationUnit("Test.java", code("Test"), true,
        null);
  }

  private ICompilationUnit generatedSleepingCode(
      String clazz, long sleepingMillis) throws JavaModelException {
    return pkg.createCompilationUnit(
        clazz + ".java", sleepingCode(clazz, sleepingMillis), true, null);
  }

  private Instant instant(long time) {
    return new Instant(time);
  }

  private ILaunch launch(ICompilationUnit unit, String mode)
      throws CoreException {
    ILaunchConfiguration config = createJavaLaunchConfig(unit);
    configsToDelete.add(config);
    return config.launch(mode, null, true);
  }

  private IClasspathEntry newEntry(IVMInstall vm) {
    return newContainerEntry(new Path(JRE_CONTAINER)
        .append(vm.getVMInstallType().getId())
        .append(vm.getName()));
  }

  private LaunchTracker newTracker(DebugPlugin plugin) {
    return new LaunchTracker(bus, clock, plugin);
  }

  private LaunchTracker newTracker(
      EventBus bus, IClock clock, DebugPlugin plugin) {
    return new LaunchTracker(bus, clock, plugin);
  }

  private Set<IPath> noFiles() {
    return emptySet();
  }

  private void putBreakpointAtLine(int line, ICompilationUnit unit)
      throws CoreException {
    createLineBreakpoint(
        unit.getResource(), // resource
        unit.getTypes()[0].getFullyQualifiedName(), // typeName
        line,
        -1, // charStart
        -1, // chartEnd
        0, // hitCount
        true, // register
        null); // attributes
  }

  private ILaunch run(ICompilationUnit unit) throws CoreException {
    return launch(unit, RUN_MODE);
  }

  private ILaunch runGeneratedCode() throws CoreException {
    return run(generatedCode());
  }

  private String sleepingCode(String clazz, long sleepMillis) {
    return "\n package " + pkg.getElementName() + ";" +
        "\n public class " + clazz + " {" +
        "\n   public static void main(String[] args) throws Exception {" +
        "\n     Thread.sleep(" + sleepMillis + ");" +
        "\n   }" +
        "\n }";
  }

  private EventBus verifyOnPost(EventBus bus, VerificationMode mode)
      throws InterruptedException {

    long tries = 100;
    while (true) {
      try {
        verify(bus, mode).post(any());
        return verify(bus, mode);

      } catch (MockitoAssertionError e) {
        if (tries-- <= 0)
          fail(e.getMessage());

        sleep(100);
      }
    }
  }
}
