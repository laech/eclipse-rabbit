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

package rabbit.tracking.tests;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.views.IViewDescriptor;

public final class TestWorkbenches {

  public static IWorkbenchPart activate(final IWorkbenchPart part) {
    syncExec(new Runnable() {
      @Override public void run() {
        part.getSite().getPage().activate(part);
      }
    });
    return part;
  }

  public static void close(final IWorkbenchWindow window) {
    syncExec(new Runnable() {
      @Override public void run() {
        if (window.getShell() != null)
          window.close();
      }
    });
  }

  public static void close(Iterable<IWorkbenchWindow> windows) {
    for (IWorkbenchWindow window : windows)
      close(window);
  }

  public static void closePartsOf(final IWorkbenchWindow window) {
    syncExec(new Runnable() {
      @Override public void run() {
        IWorkbenchPage page = window.getActivePage();
        page.closeAllEditors(false);
        for (IViewReference ref : page.getViewReferences())
          page.hideView(ref);
      }
    });
  }

  public static void closePartsOfCurrentWindow() {
    closePartsOf(currentWindow());
  }

  public static void closePerspective(
      final IPerspectiveDescriptor perspective, final IWorkbenchWindow window) {
    syncExec(new Runnable() {
      @Override public void run() {
        window.getActivePage().closePerspective(perspective, false, false);
      }
    });
  }

  public static void closePerspectivesOf(final IWorkbenchWindow window) {
    syncExec(new Runnable() {
      @Override public void run() {
        window.getActivePage().closeAllPerspectives(false, false);
      }
    });
  }

  public static void closePerspectivesOfCurrentWindow() {
    closePerspectivesOf(currentWindow());
  }

  public static void hide(final IViewPart view) {
    syncExec(new Runnable() {
      @Override public void run() {
        view.getSite().getPage().hideView(view);
      }
    });
  }

  public static IViewPart openRandomPartOn(final IWorkbenchWindow win) {
    final IViewPart[] view = new IViewPart[1];
    syncExec(new Runnable() {
      @Override public void run() {
        String currentId = getCurrentViewId(win);
        for (IViewDescriptor v : getViews(win)) {
          if (!v.getId().equals(currentId)) {
            try {
              view[0] = win.getActivePage().showView(v.getId());
            } catch (PartInitException e) {
              throw new RuntimeException(e);
            }
            return;
          }
        }
        throw new RuntimeException("Don't have more view to show");
      }

      private String getCurrentViewId(IWorkbenchWindow win) {
        IWorkbenchPart current = win.getActivePage().getActivePart();
        return current != null ? current.getSite().getId() : null;
      }

      private IViewDescriptor[] getViews(IWorkbenchWindow win) {
        return win.getWorkbench().getViewRegistry().getViews();
      }
    });
    return view[0];
  }

  public static IViewPart openRandomPartOnCurrentWindow() {
    return openRandomPartOn(currentWindow());
  }

  public static IPerspectiveDescriptor openRandomPerspectiveOn(
      final IWorkbenchWindow window) {

    final IPerspectiveDescriptor[] perspective = new IPerspectiveDescriptor[1];
    syncExec(new Runnable() {
      @Override public void run() {
        String currentId = getCurrentPerspectiveId(window);
        for (IPerspectiveDescriptor p : getPerspectives(window)) {
          if (!p.getId().equals(currentId)) {
            perspective[0] = p;
            window.getActivePage().setPerspective(p);
            return;
          }
        }
        throw new RuntimeException("Don't have more perspective to show");
      }

      private String getCurrentPerspectiveId(final IWorkbenchWindow window) {
        IPerspectiveDescriptor current = currentPerspectiveOf(window);
        return current != null ? current.getId() : null;
      }

      private IPerspectiveDescriptor[] getPerspectives(IWorkbenchWindow window) {
        return window.getWorkbench().getPerspectiveRegistry().getPerspectives();
      }
    });
    return perspective[0];
  }

  public static IPerspectiveDescriptor openRandomPerspectiveOnCurrentWindow() {
    return openRandomPerspectiveOn(currentWindow());
  }

  public static IWorkbenchWindow openWindow() {
    final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
    syncExec(new Runnable() {
      @Override public void run() {
        try {
          window[0] = getWorkbench().openWorkbenchWindow(null);
        } catch (WorkbenchException e) {
          throw new RuntimeException(e);
        }
        window[0].getShell().setActive();
      }
    });
    return window[0];
  }

  private static IPerspectiveDescriptor currentPerspectiveOf(
      IWorkbenchWindow window) {
    return window.getActivePage().getPerspective();
  }

  private static IWorkbenchWindow currentWindow() {
    final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
    syncExec(new Runnable() {
      @Override public void run() {
        window[0] = getWorkbench().getActiveWorkbenchWindow();
      }
    });
    return window[0];
  }

  private static void syncExec(Runnable code) {
    getWorkbench().getDisplay().syncExec(code);
  }

  private TestWorkbenches() {
  }
}
