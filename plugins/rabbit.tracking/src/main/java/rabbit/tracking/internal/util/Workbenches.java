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

package rabbit.tracking.internal.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public final class Workbenches {

  /**
   * Gets the current workbench window in focus.
   * 
   * @param workbench the workbench to get the focused window for
   * @return the currently focused window, or null if none
   * @throws NullPointerException if workbench is null
   */
  public static IWorkbenchWindow getFocusedWindow(final IWorkbench workbench) {
    check(workbench);

    if (Display.getCurrent() != null) {
      IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
      if (window == null) {
        return null;
      }
      return isFocused(window) ? window : null;
    }

    final IWorkbenchWindow[] result = {null};
    workbench.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window != null && isFocused(window)) {
          result[0] = window;
        }
      }
    });
    return result[0];
  }

  /**
   * Gets the current workbench part in focus.
   * 
   * @param workbench the workbench to get the focused part for
   * @return the currently focused workbench part, or null if none
   * @throws NullPointerException if workbench is null
   */
  public static IWorkbenchPart getFocusedPart(IWorkbench workbench) {
    IWorkbenchWindow window = getFocusedWindow(check(workbench));
    return window != null ? window.getPartService().getActivePart() : null;
  }

  /**
   * Gets the current perspective in focus.
   * 
   * @param workbench the workbench to get the focused perspective for
   * @return the current focused perspective, or null if none
   * @throws NullPointerException if workbench is null
   */
  public static IPerspectiveDescriptor getFocusedPerspective(
      IWorkbench workbench) {
    IWorkbenchWindow window = getFocusedWindow(workbench);
    if (window == null) {
      return null;
    }
    IWorkbenchPage page = window.getActivePage();
    return page == null ? null : page.getPerspective();
  }

  /**
   * Gets all the {@link IPartService}s from the currently opened windows.
   * 
   * @param workbench the workbench to get all the part service for
   * @return a set of all {@link IPartService}s, not null, may be empty,
   *         unmodifiable
   * @throws NullPointerException if workbench is null
   */
  public static Set<IPartService> getPartServices(IWorkbench workbench) {
    IWorkbenchWindow[] windows = check(workbench).getWorkbenchWindows();
    Set<IPartService> services = newHashSetWithExpectedSize(windows.length);
    for (IWorkbenchWindow window : windows) {
      services.add(window.getPartService());
    }
    return unmodifiableSet(services);
  }

  private static boolean isFocused(IWorkbenchWindow window) {
    Shell shell = window.getShell();
    return shell.getDisplay().getActiveShell() == shell
        && !shell.getMinimized();
  }

  private static IWorkbench check(IWorkbench workbench) {
    return checkNotNull(workbench, "workbench");
  }

  private Workbenches() {
  }
}
