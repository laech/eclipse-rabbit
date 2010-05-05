/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.tracking.internal.trackers;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.JavaEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;

import com.google.common.base.Objects;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("restriction")
public class JavaTracker extends AbstractTracker<JavaEvent> 
    implements IWindowListener, IPartListener, Observer {
  
  private ScheduledThreadPoolExecutor timer;
//  private final Runnable code;
  
  /**
   * Current selected java element, may be null;
   */
  private IJavaElement currentElement = null;
  private long startTime = -1;
  
  private Runnable tryStartSessionRunnable = new Runnable() {
    
    @Override
    public void run() {
      tryStartSession();
    }
  };
  
  public JavaTracker() {
    super();
  }
  
  @Override
  public void partActivated(IWorkbenchPart part) {
    tryStartSession();
  }
  
  @Override
  public void partBroughtToTop(IWorkbenchPart part) {
    // Do nothing.
  }
  
  @Override
  public void partClosed(IWorkbenchPart part) {
    // Do nothing, parts are deactivated before it can be closed.
  }

  @Override
  public void partDeactivated(IWorkbenchPart part) {
    tryEndSession();
  }

  @Override
  public void partOpened(IWorkbenchPart part) {
    // Do nothing, if this part is active, partActivated will be call after this
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o != TrackingPlugin.getDefault().getIdleDetector() || !isEnabled()) {
      return;
    }
    
    if (((IdleDetector) o).isUserActive()) {
      PlatformUI.getWorkbench().getDisplay().syncExec(tryStartSessionRunnable);
    } else {
      tryEndSession();
    }
  }

  @Override
  public void windowActivated(IWorkbenchWindow window) {
    tryStartSession();
  }

  @Override
  public void windowClosed(IWorkbenchWindow window) {
    window.getPartService().removePartListener(this);
    tryEndSession();
  }

  @Override
  public void windowDeactivated(IWorkbenchWindow window) {
    tryEndSession();
  }

  @Override
  public void windowOpened(IWorkbenchWindow window) {
    window.getPartService().addPartListener(this);
    tryStartSession();
  }

  @Override
  protected IStorer<JavaEvent> createDataStorer() {
    return DataHandler.getStorer(JavaEvent.class);
  }

  @Override
  protected void doDisable() {
    tryEndSession();
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(this);
    
    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.removeWindowListener(this);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      window.getPartService().removePartListener(this);
    }
    if (timer != null) {
      timer.shutdownNow();
      timer = null;
    }
  }

  @Override
  protected void doEnable() {
    if (timer != null) {
      timer.shutdownNow();
    }
    Runnable code = new Runnable() {
      @Override public void run() {
        PlatformUI.getWorkbench().getDisplay().syncExec(tryStartSessionRunnable);
      }
    };
    timer = new ScheduledThreadPoolExecutor(1);
    timer.scheduleAtFixedRate(code, 2, 2, TimeUnit.SECONDS);
    
    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.addWindowListener(this);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      window.getPartService().addPartListener(this);
    }
    
    TrackingPlugin.getDefault().getIdleDetector().addObserver(this);
  }

  /**
   * Reset to indicate no session is being tracked.
   */
  private void reset() {
    startTime = -1;
    currentElement = null;
  }

  /**
   * Ends a tracking session if there is one, and calls {@link #reset()} after.
   */
  private void tryEndSession() {
    if (startTime < 0) {
      return;
    }
        
    long duration = System.currentTimeMillis() - startTime;
    if (duration > 0) {
      addData(new JavaEvent(new DateTime(), duration, currentElement));
      System.out.println(currentElement.getHandleIdentifier() + ": " + duration);
    }
    reset();
  }

  /**
   * Tries to start a tracking session, if the current element is not change,
   * will do nothing, otherwise ends a session if there is one running, then if
   * the currently selected element in Eclipse's active editor is not null, 
   * starts a new session.
   * <p>
   * <strong>NOTE:</strong> Run this in the UI thread.
   * </p>
   */
  private void tryStartSession() {
    if (!TrackingPlugin.getDefault().getIdleDetector().isUserActive()) {
      return;
    }
    
    IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (win == null) {
      return;
    }
    
    IWorkbenchPart part = win.getPartService().getActivePart();
    if (!(part instanceof JavaEditor)) {
      return;
    }
    
    JavaEditor editor = (JavaEditor) part;
    IJavaElement element = null;
    try {
      element = SelectionConverter.getElementAtOffset(editor);
    } catch (JavaModelException e) {
      element = null;
    }
    
    // If it's the same element, do nothing
    if (Objects.equal(currentElement, element) && currentElement != null) {
      return;
    }
    
    tryEndSession();
    if (element != null) {
      currentElement = element;
      startTime = System.currentTimeMillis();
    }
  }
}
