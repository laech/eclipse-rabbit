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

package rabbit.tracking.internal.util

import java.util.concurrent.atomic.AtomicReference
import scala.collection.JavaConversions.asScalaSet
import org.eclipse.swt.widgets.{ Shell, Display }
import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart, IWorkbench, IPartService }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{ FlatSpec, BeforeAndAfter }
import rabbit.tracking.tests.TestImplicits.funToRunnable
import rabbit.tracking.tests.Workbenches.{ openWindow, openRandomPart, openRandomPerspective }
import org.eclipse.ui.IPerspectiveDescriptor

@RunWith(classOf[JUnitRunner])
final class WorkbenchesSpec extends FlatSpec with MustMatchers with BeforeAndAfter {

  private var display: Display = _
  private var workbench: IWorkbench = _

  before {
    workbench = getWorkbench
    display = workbench.getDisplay
  }

  behavior of "Workbenches.getFocusedWindow"

  it must "throw NullPointerException if workbench is null" in {
    intercept[NullPointerException] {
      Workbenches.getFocusedWindow(null)
    }
  }

  it must "be able to return the focused window from the UI thread" in {
    val actual = new AtomicReference[IWorkbenchWindow]
    display.syncExec(() => {
      actual set Workbenches.getFocusedWindow(workbench)
    })
    actual.get must not be (null)
    actual.get must be(currentWindow)
  }

  it must "be able to return the focused window from a non-UI thread" in {
    val actual = new AtomicReference[IWorkbenchWindow]
    val thread = new Thread(() => {
      actual set Workbenches.getFocusedWindow(workbench)
    })
    thread.start()
    thread.join()
    actual.get must not be (null)
    actual.get must be(currentWindow)
  }

  it must "return null if there are no window in focus" in {
    var actual = new AtomicReference[IWorkbenchWindow]
    display.syncExec(() => {
      val dialog = openDialog()
      actual set Workbenches.getFocusedWindow(workbench)
      dialog.close()
    })
    actual.get must be(null)
  }

  behavior of "Workbenches.getFocusedPart"

  it must "throw NullPointerException if workbench is null" in {
    intercept[NullPointerException] {
      Workbenches.getFocusedPart(null)
    }
  }

  it must "return the part in focus" in {
    val expected = openRandomPart
    val actual = Workbenches.getFocusedPart(workbench)
    actual must be(expected)
  }

  it must "return null if there is no part in focus" in {
    openRandomPart
    var actual = new AtomicReference[IWorkbenchPart]
    display.syncExec(() => {
      val dialog = openDialog()
      actual set Workbenches.getFocusedPart(workbench)
      dialog.close()
    })
    actual.get must be(null)
  }

  behavior of "Workbenches.getFocusedPerspective"

  it must "return null if there is no perspective in focus" in {
    intercept[NullPointerException] {
      Workbenches.getFocusedPerspective(null)
    }
  }

  it must "return the perspective in focus" in {
    val expected = openRandomPerspective()
    val actual = Workbenches.getFocusedPerspective(workbench)
    actual must be(expected)
  }

  it must "throw NullPointerException if workbench is null" in {
    openRandomPerspective()
    var actual = new AtomicReference[IPerspectiveDescriptor]
    display.syncExec(() => {
      val dialog = openDialog()
      actual set Workbenches.getFocusedPerspective(workbench)
      dialog.close()
    })
    actual.get must be(null)
  }

  behavior of "Workbenches.getPartServices"

  it must "throw NullPointerException if workbench is null" in {
    intercept[NullPointerException] {
      Workbenches.getPartServices(null)
    }
  }

  it must "return all the part services of all windows" in {
    val current = currentWindow
    val windows = Seq(current, openWindow, openWindow)
    try {
      val expected = windows.map(_.getPartService).toSet
      val actual: collection.mutable.Set[IPartService] = Workbenches.getPartServices(workbench)
      actual must be(expected)
    } finally {
      display.syncExec(() => {
        windows.filter(_ != current).foreach(_.close)
      })
    }
  }

  private def currentWindow = {
    var ref = new AtomicReference[IWorkbenchWindow]
    display.syncExec(() => {
      ref set workbench.getActiveWorkbenchWindow
    })
    ref.get
  }

  private def openDialog() = {
    val shell = currentWindow.getShell
    val dialog = new Shell(shell)
    dialog.setSize(100, 100)
    dialog.open()
    dialog
  }
}