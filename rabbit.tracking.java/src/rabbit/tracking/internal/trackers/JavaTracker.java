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

import com.google.common.collect.Sets;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

@SuppressWarnings("restriction")
public class JavaTracker extends AbstractTracker<JavaEvent> 
    implements IWindowListener, IPartListener, Observer, Listener {
  
  /**
   * Current selected java element, may be null;
   */
  private IJavaElement currentElement = null;
  private long startTime = -1;
  
  /**
   * A set of all text widgets that are currently being listened to.
   * This set is not synchronized.
   */
  private final Set<StyledText> registeredWidgets;
  
  public JavaTracker() {
    super();
    registeredWidgets = Sets.newHashSet();
  }
  
  @Override
  public void partActivated(IWorkbenchPart part) {
    tryStartSession(part);
  }
  
  @Override
  public void partBroughtToTop(IWorkbenchPart part) {
    // Do nothing.
  }
  
  @Override
  public void partClosed(IWorkbenchPart part) {
    if (part instanceof JavaEditor) {
      deregister((JavaEditor) part);
    }
  }

  @Override
  public void partDeactivated(IWorkbenchPart part) {
    tryEndSession();
  }

  @Override
  public void partOpened(IWorkbenchPart part) {
    if (part instanceof JavaEditor) {
      register((JavaEditor) part);
    }
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o != TrackingPlugin.getDefault().getIdleDetector() || !isEnabled()) {
      return;
    }
    
    if (((IdleDetector) o).isUserActive()) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
        @Override public void run() {
          IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          if (win != null) {
            tryStartSession(win.getPartService().getActivePart());
          }
        }
      });
    } else {
      tryEndSession();
    }
  }

  @Override
  public void windowActivated(IWorkbenchWindow window) {
    tryStartSession(window.getPartService().getActivePart());
  }

  @Override
  public void windowClosed(IWorkbenchWindow window) {
    tryEndSession();
    deregister(window);
    
    window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null) {
      tryStartSession(window.getPartService().getActivePart());
    }
  }

  @Override
  public void windowDeactivated(IWorkbenchWindow window) {
    tryEndSession();
  }

  @Override
  public void windowOpened(IWorkbenchWindow window) {
    register(window);
    if (window.getWorkbench().getActiveWorkbenchWindow() == window) {
      tryStartSession(window.getPartService().getActivePart());
    }
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
      deregister(window);
    }
  }
  
  @Override
  protected void doEnable() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.addWindowListener(this);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      register(window);
    }
    
    TrackingPlugin.getDefault().getIdleDetector().addObserver(this);
  }
  
  private synchronized void deregister(JavaEditor editor) {
    final StyledText widget = editor.getViewer().getTextWidget();
    if (registeredWidgets.contains(widget)) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
        @Override
        public void run() {
          widget.removeListener(SWT.KeyDown, JavaTracker.this);
          widget.removeListener(SWT.MouseDown, JavaTracker.this);
        }
      });
      registeredWidgets.remove(registeredWidgets);
    }
  }
  
  private synchronized void register(JavaEditor editor) {
    final StyledText widget = editor.getViewer().getTextWidget();
    if (!registeredWidgets.contains(widget)) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
        @Override
        public void run() {
          widget.addListener(SWT.KeyDown, JavaTracker.this);
          widget.addListener(SWT.MouseDown, JavaTracker.this);
        }
      });
      registeredWidgets.add(widget);
    }
  }
  
  private void deregister(IWorkbenchWindow window) {
    window.getPartService().removePartListener(this);
    for (IWorkbenchPage page : window.getPages()) {
      for (IEditorReference ref : page.getEditorReferences()) {
        IEditorPart editor = ref.getEditor(false);
        if (editor instanceof JavaEditor) {
          deregister((JavaEditor) editor);
        }
      }
    }
  }
  
  private void register(IWorkbenchWindow window) {
    window.getPartService().addPartListener(this);
    for (IWorkbenchPage page : window.getPages()) {
      for (IEditorReference ref : page.getEditorReferences()) {
        IEditorPart editor = ref.getEditor(false);
        if (editor instanceof JavaEditor) {
          register((JavaEditor) editor);
        }
      }
    }
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
   * 
   * @param activePart The currently active part of the workbench, may be null.
   */
  private void tryStartSession(IWorkbenchPart activePart) {
    
    if (!(activePart instanceof JavaEditor)) {
      if (currentElement != null) {
        tryEndSession();
      }
      return;
    }

    IJavaElement element = null;
    try {
      element = SelectionConverter.getElementAtOffset((JavaEditor) activePart);
    } catch (JavaModelException e) {
      element = null;
    }

    // If it's the same element, do nothing
    if (currentElement != null && currentElement.equals(element)) {
      return;
    }

    tryEndSession();
    if (element != null) {
      currentElement = element;
      startTime = System.currentTimeMillis();
    }
    System.out.println(startTime);
  }
  
  /**
   * UI thread.
   */
  private void tryStartSession() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null) {
      tryStartSession(window.getPartService().getActivePart());
    }
  }

  /*
   * Listener to listen to keyboard input and mouse input on text widgets of
   * editors.
   */
  @Override
  public void handleEvent(Event event) {
    tryStartSession();
  }
}
