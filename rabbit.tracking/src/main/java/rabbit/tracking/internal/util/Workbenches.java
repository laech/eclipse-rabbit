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

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static java.util.Collections.unmodifiableSet;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public final class Workbenches {

  /**
   * Gets the current workbench window in focus.
   * 
   * @return the currently focused window, or null if none
   */
  public static IWorkbenchWindow getFocusedWindow() {
    if (Display.getCurrent() != null) {
      IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
      return isFocused(window) ? window : null;
    }

    final AtomicReference<IWorkbenchWindow> result = newAtomicReference();
    syncExec(new Runnable() {
      @Override public void run() {
        IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
        if (isFocused(window)) {
          result.set(window);
        }
      }
    });
    return result.get();
  }

  /**
   * Gets the current workbench part in focus.
   * 
   * @return the currently focused workbench part, or null if none
   */
  public static IWorkbenchPart getFocusedPart() {
    IWorkbenchWindow window = getFocusedWindow();
    return window != null ? window.getPartService().getActivePart() : null;
  }

  /**
   * Gets all the {@link IPartService}s from the currently opened windows.
   * 
   * @return a set of all {@link IPartService}s, not null, may be empty,
   *         unmodifiable
   */
  public static Set<IPartService> getPartServices() {
    IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
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

  private static void syncExec(Runnable runnable) {
    getWorkbench().getDisplay().syncExec(runnable);
  }

  private static <T> AtomicReference<T> newAtomicReference() {
    return new AtomicReference<T>();
  }

  private Workbenches() {
  }
}
