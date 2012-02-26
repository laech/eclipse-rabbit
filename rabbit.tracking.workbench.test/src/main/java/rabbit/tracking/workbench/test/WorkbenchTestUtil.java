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

package rabbit.tracking.workbench.test;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.Random;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.views.IViewDescriptor;

public class WorkbenchTestUtil {

  public static IWorkbenchPart activate(final IWorkbenchPart part) {
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        part.getSite().getPage().activate(part);
      }
    });
    return part;
  }

  public static void close(final IWorkbenchWindow window) {
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        window.close();
      }
    });
  }

  public static void closeAllParts() {
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        closeAllParts(getWorkbench().getActiveWorkbenchWindow());
      }
    });
  }

  public static void closeAllParts(final IWorkbenchWindow window) {
    window.getShell().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        IWorkbenchPage page = window.getActivePage();
        page.closeAllEditors(false);
        IViewReference[] views = page.getViewReferences();
        for (IViewReference view : views) {
          page.hideView(view);
        }
      }
    });
  }

  public static void hide(final IViewPart view) {
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        view.getSite().getPage().hideView(view);
      }
    });
  }

  public static IViewPart openRandomPart() {
    final IViewPart[] part = new IViewPart[1];
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        part[0] = openRandomPart(getWorkbench().getActiveWorkbenchWindow());
      }
    });
    return part[0];
  }

  public static IViewPart openRandomPart(final IWorkbenchWindow window) {
    final IViewPart[] part = new IViewPart[1];
    window.getShell().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        IViewDescriptor[] views = getWorkbench().getViewRegistry().getViews();
        String viewId = views[new Random().nextInt(views.length)].getId();
        try {
          part[0] = window.getActivePage().showView(viewId);
        } catch (PartInitException e) {
          throw new RuntimeException(e);
        }
      }
    });
    return part[0];
  }

  public static IWorkbenchWindow openWindow() {
    final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
    getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        try {
          window[0] = getWorkbench().openWorkbenchWindow(null);
          window[0].getShell().setActive();
        } catch (WorkbenchException e) {
          throw new RuntimeException(e);
        }
      }
    });
    return window[0];
  }

  private WorkbenchTestUtil() {
  }
}
