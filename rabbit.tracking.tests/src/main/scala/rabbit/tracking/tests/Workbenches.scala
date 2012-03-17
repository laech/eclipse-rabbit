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

package rabbit.tracking.tests

import java.util.Random
import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart, IViewPart }
import rabbit.tracking.tests.TestImplicits.funToRunnable
import org.eclipse.ui.IPerspectiveDescriptor

object Workbenches {

  def activate(part: IWorkbenchPart) = {
    display.syncExec(() => {
      part.getSite.getPage.activate(part)
    })
    part
  }

  def close(window: IWorkbenchWindow) = display.syncExec(() => {
    window.close()
  })

  def closeAllParts(): Unit = closeAllParts(getWorkbench.getActiveWorkbenchWindow)

  def closeAllParts(window: IWorkbenchWindow) = display.syncExec(() => {
    val page = window.getActivePage
    page.closeAllEditors(false)
    page.getViewReferences.foreach(page.hideView)
  })

  def closeAllPerspectives() = display.syncExec(() => {
    currentWindow.getActivePage.closeAllPerspectives(false, false)
  })

  def hide(view: IViewPart) = display.syncExec(() => {
    view.getSite.getPage.hideView(view)
  })

  def openRandomPerspective(): IPerspectiveDescriptor =
    openRandomPerspective(currentWindow)

  def openRandomPerspective(window: IWorkbenchWindow) = {
    var perspective: IPerspectiveDescriptor = null
    display.syncExec(() => {
      val current = window.getActivePage.getPerspective
      val currentId = if (current != null) current.getId else null
      val perspectives = window.getWorkbench.getPerspectiveRegistry.getPerspectives
      val page = window.getActivePage
      perspectives find (_.getId != currentId) match {
        case Some(_@ perspective) => page.setPerspective(perspective)
        case None => throw new RuntimeException("Don't have more perspective to show")
      }
    })
    perspective
  }

  def openRandomPart(): IViewPart =
    openRandomPart(currentWindow)

  def openRandomPart(window: IWorkbenchWindow) = {
    var view: IViewPart = null
    display.syncExec(() => {
      val current = window.getActivePage.getActivePart
      val currentId = if (current != null) current.getSite.getId else null
      val views = window.getWorkbench.getViewRegistry.getViews
      val page = window.getActivePage
      views.find(_.getId != currentId) match {
        case Some(v) => view = window.getActivePage.showView(v.getId)
        case None => throw new RuntimeException("Don't have more view to show")
      }
    })
    view
  }

  def openWindow() = {
    var window: IWorkbenchWindow = null
    display.syncExec(() => {
      window = getWorkbench.openWorkbenchWindow(null)
      window.getShell.setActive
    })
    window
  }

  def currentWindow = {
    var window: IWorkbenchWindow = null
    display.syncExec(() => {
      window = getWorkbench.getActiveWorkbenchWindow
    })
    window
  }

  private def display = getWorkbench.getDisplay
}