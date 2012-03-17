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

import scala.collection.JavaConversions.asScalaSet

import org.eclipse.swt.widgets.{ Shell, Display }
import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart, IWorkbench, IPartService }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{ FlatSpec, BeforeAndAfter }

import rabbit.tracking.tests.TestImplicits.funToRunnable
import rabbit.tracking.tests.Workbenches.{ openWindow, openRandomPart }

@RunWith(classOf[JUnitRunner])
final class WorkbenchesSpec extends FlatSpec with MustMatchers with BeforeAndAfter {

  private var display: Display = _
  private var workbench: IWorkbench = _

  before {
    workbench = getWorkbench
    display = workbench.getDisplay
  }

  behavior of "Workbenches.getFocusedWindow"

  it must "return the window in focus" in {
    val window = Workbenches.getFocusedWindow
    window must not be (null)
    window must be(currentWindow)
  }

  it must "return null if there are no window in focus" in {
    var window: IWorkbenchWindow = null
    display.syncExec(() => {
      val dialog = openDialog()
      window = Workbenches.getFocusedWindow
      dialog.close()
    })
    window must be(null)
  }

  behavior of "Workbenches.getFocusedPart"

  it must "return the part in focus" in {
    val expected = openRandomPart
    val actual = Workbenches.getFocusedPart
    actual must be(expected)
  }

  it must "return nll if there are no part in focus" in {
    openRandomPart
    var part: IWorkbenchPart = null
    display.syncExec(() => {
      val dialog = openDialog()
      part = Workbenches.getFocusedPart
      dialog.close()
    })
    part must be(null)
  }

  behavior of "Workbenches.getPartServices"

  it must "return all the part services of all windows" in {
    val windows = Seq(currentWindow, openWindow, openWindow)
    try {
      val expected = windows.map(_.getPartService).toSet
      val actual: collection.mutable.Set[IPartService] = Workbenches.getPartServices
      actual must be(expected)
    } finally {
      display.syncExec(() => {
        windows.foreach(_.close)
      })
    }
  }

  private def currentWindow = {
    var window: IWorkbenchWindow = null
    display.syncExec(() => {
      window = workbench.getActiveWorkbenchWindow
    })
    window
  }

  private def openDialog() = {
    val shell = currentWindow.getShell
    val dialog = new Shell(shell)
    dialog.setSize(100, 100)
    dialog.open()
    dialog
  }
}