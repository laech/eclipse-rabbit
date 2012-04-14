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

package rabbit.tracking;

import static com.google.common.base.Preconditions.checkNotNull;
import static rabbit.tracking.internal.util.Workbenches.getFocusedWindow;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import com.google.inject.Inject;

/**
 * Tracks the currently focused perspective, across multiple workbench windows.
 * See {@link IPerspectiveFocusListener} for behaviors of this tracker.
 * 
 * @since 2.0
 */
final class PerspectiveFocusTracker
    extends AbstractListenableTracker<IPerspectiveFocusListener> {

  private class MyWindowListener implements IWindowListener {
    @Override public void windowOpened(IWorkbenchWindow window) {
      window.addPerspectiveListener(perspectiveListener);
    }

    @Override public void windowClosed(IWorkbenchWindow window) {
      window.removePerspectiveListener(perspectiveListener);
    }

    @Override public void windowDeactivated(IWorkbenchWindow window) {
      IPerspectiveDescriptor perspective = getPerspective(window);
      if (perspective != null) {
        onPerspectiveUnfocused(perspective);
      }
    }

    @Override public void windowActivated(IWorkbenchWindow window) {
      IPerspectiveDescriptor perspective = getPerspective(window);
      if (perspective != null) {
        onPerspectiveFocused(perspective);
      }
    }
  }

  private class MyPerspectiveListener extends PerspectiveAdapter {
    @Override public void perspectiveActivated(
        IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      super.perspectiveActivated(page, perspective);
      if (page.getWorkbenchWindow() == getFocusedWindow(workbench)) {
        onPerspectiveFocused(perspective);
      }
    }

    @Override public void perspectiveDeactivated(
        IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      super.perspectiveDeactivated(page, perspective);
      if (page.getWorkbenchWindow() == getFocusedWindow(workbench)) {
        onPerspectiveUnfocused(perspective);
      }
    }

    @Override public void perspectiveClosed(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
      super.perspectiveClosed(page, perspective);
      if (page.getWorkbenchWindow() == getFocusedWindow(workbench)
          && page.getPerspective() == perspective) {
        onPerspectiveUnfocused(perspective);
      }
    }
  }

  private final IWorkbench workbench;
  private final IWindowListener windowListener;
  private final IPerspectiveListener perspectiveListener;

  /**
   * @param workbench the workbench to track, not null
   * @throws NullPointerException if workbench is null
   */
  @Inject PerspectiveFocusTracker(IWorkbench workbench) {
    this.workbench = checkNotNull(workbench, "workbench");
    this.windowListener = new MyWindowListener();
    this.perspectiveListener = new MyPerspectiveListener();
  }

  @Override protected void onEnable() {
    workbench.addWindowListener(windowListener);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      window.addPerspectiveListener(perspectiveListener);
    }
  }

  @Override protected void onDisable() {
    workbench.removeWindowListener(windowListener);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      window.removePerspectiveListener(perspectiveListener);
    }
  }

  private IPerspectiveDescriptor getPerspective(IWorkbenchWindow window) {
    IWorkbenchPage page = window.getActivePage();
    return page == null ? null : page.getPerspective();
  }

  private void onPerspectiveFocused(IPerspectiveDescriptor perspective) {
    for (IPerspectiveFocusListener listener : getListeners()) {
      listener.onPerspectiveFocused(perspective);
    }
  }

  private void onPerspectiveUnfocused(IPerspectiveDescriptor perspective) {
    for (IPerspectiveFocusListener listener : getListeners()) {
      listener.onPerspectiveUnfocused(perspective);
    }
  }
}
