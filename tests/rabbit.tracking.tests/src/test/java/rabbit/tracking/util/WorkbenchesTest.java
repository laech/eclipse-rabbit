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

package rabbit.tracking.util;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static rabbit.tracking.internal.util.Workbenches.allPartServicesOf;
import static rabbit.tracking.internal.util.Workbenches.focusedPartOf;
import static rabbit.tracking.internal.util.Workbenches.focusedPerspectiveOf;
import static rabbit.tracking.internal.util.Workbenches.focusedWindowOf;
import static rabbit.tracking.tests.TestWorkbenches.close;
import static rabbit.tracking.tests.TestWorkbenches.currentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPartOnCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPerspectiveOnCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openWindow;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;

public final class WorkbenchesTest {

  private abstract class TestRunnable implements Runnable {
    @Override public final void run() {
      try {
        test();
      } catch (Throwable t) {
        error = t;
      }
    }

    protected abstract void test();
  }

  private volatile Throwable error;

  @After public void after() throws Throwable {
    if (error != null)
      throw error;
  }

  @Before public void before() {
    error = null;
  }

  @Test public void findsFocusedPart() {
    IWorkbenchPart expected = openRandomPartOnCurrentWindow();
    IWorkbenchPart actual = focusedPartOf(currentWorkbench());
    assertThat(actual, is(expected));
  }

  @Test public void findsFocusedPerspective() {
    IPerspectiveDescriptor expected = openRandomPerspectiveOnCurrentWindow();
    IPerspectiveDescriptor actual = focusedPerspectiveOf(currentWorkbench());
    assertThat(actual, is(expected));
  }

  @Test public void findsFocusedWindowFromNonUiThread() {
    runInNewThread(new TestRunnable() {
      @SuppressWarnings("unchecked")//
      @Override public void test() {
        assertThat(focusedWindowOf(currentWorkbench()),
            allOf(notNullValue(), is(currentWindow())));
      }
    });
  }

  @Test public void findsFocusedWindowFromUiThread() {
    runInUiThread(new TestRunnable() {
      @SuppressWarnings("unchecked")//
      @Override public void test() {
        assertThat(focusedWindowOf(currentWorkbench()),
            allOf(notNullValue(), is(currentWindow())));
      }
    });
  }

  @Test public void findsNullIfNoPartIsFocused() {
    openRandomPerspectiveOnCurrentWindow();
    runInUiThread(withNoFocusedWindow(new Runnable() {
      @Override public void run() {
        assertThat(focusedPartOf(currentWorkbench()), is(nullValue()));
      }
    }));
  }

  @Test public void findsNullIfNoPerspectiveIsFocused() {
    openRandomPerspectiveOnCurrentWindow();
    runInUiThread(withNoFocusedWindow(new Runnable() {
      @Override public void run() {
        assertThat(focusedPerspectiveOf(currentWorkbench()), is(nullValue()));
      }
    }));
  }

  @Test public void findsNullWhenNoWindowIsFocused() {
    runInUiThread(withNoFocusedWindow(new Runnable() {
      @Override public void run() {
        assertThat(focusedWindowOf(currentWorkbench()), is(nullValue()));
      }
    }));
  }

  @Test public void findsPartServicesOfAllWindows() {
    List<IWorkbenchWindow> windows = asList(
        currentWindow(), openWindow(), openWindow());
    try {
      Set<IPartService> expected = getPartServices(windows);
      Set<IPartService> actual = allPartServicesOf(currentWorkbench());
      assertThat(actual, is(expected));
    } finally {
      close(windows.subList(1, windows.size()));
    }
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnNullWorkbenchWhenFindingFocusedPart() {
    focusedPartOf(null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnNullWorkbenchWhenFindingFocusedPerspective() {
    focusedPerspectiveOf(null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnNullWorkbenchWhenFindingFocusedWindow() {
    focusedWindowOf(null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnNullWorkbenchWhenFindingPartServices() {
    allPartServicesOf(null);
  }

  private IWorkbench currentWorkbench() {
    return getWorkbench();
  }

  private Set<IPartService> getPartServices(Iterable<IWorkbenchWindow> windows) {
    return newHashSet(transform(windows,
        new Function<IWorkbenchWindow, IPartService>() {
          @Override public IPartService apply(IWorkbenchWindow window) {
            return window.getPartService();
          }
        }));
  }

  private Shell openDialog() {
    Shell dialog = new Shell(currentWindow().getShell());
    dialog.setSize(100, 100);
    dialog.open();
    return dialog;
  }

  private void runInNewThread(TestRunnable code) {
    Thread thread = new Thread(code);
    thread.start();
    try {
      thread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void runInUiThread(TestRunnable code) {
    getWorkbench().getDisplay().syncExec(code);
  }

  private TestRunnable withNoFocusedWindow(final Runnable code) {
    return new TestRunnable() {
      @Override public void test() {
        Shell dialog = openDialog();
        try {
          code.run();
        } finally {
          dialog.close();
        }
      }
    };
  }
}
