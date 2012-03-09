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

package rabbit.tracking.workbench;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static rabbit.tracking.internal.workbench.util.WorkbenchUtil.getFocusedPart;
import static rabbit.tracking.internal.workbench.util.WorkbenchUtil.getPartServices;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.internal.workbench.util.PartListener;

/**
 * Tracks the currently focused workbench part in the workbench, across multiple
 * workbench windows. A part is consider focused if all of the followings are
 * true:
 * <ul>
 * <li>it's the active part in the parent window</li>
 * <li>its parent window is focused</li>
 * </ul>
 * therefore there will be at most one focused part at any time regardless of
 * how many workbench windows are opened.
 * <p/>
 * {@link IPartFocusListener#onPartFocused(IWorkbenchPart)} will be called when
 * a part becomes focused, and when a new part becomes focused,
 * {@link IPartFocusListener#onPartUnfocused(IWorkbenchPart)} will be called
 * with the old part before the new part is called with
 * {@link IPartFocusListener#onPartFocused(IWorkbenchPart)}.
 * <p/>
 * When this tracker is enabled, if there is a currently focused part,
 * {@link IPartFocusListener#onPartFocused(IWorkbenchPart)} will be notify
 * immediately.
 * 
 * @since 2.0
 */
public final class PartTracker extends AbstractTracker {

  /**
   * Listener to listen to part focus events.
   * 
   * @since 2.0
   */
  public static interface IPartFocusListener {

    /**
     * Called when a workbench part became focused.
     * 
     * @param part the workbench part in focus, not null
     */
    void onPartFocused(IWorkbenchPart part);

    /**
     * Called when the previously focused workbench part no longer has the
     * focus.
     * 
     * @param part the part that lost focus, not null
     */
    void onPartUnfocused(IWorkbenchPart part);
  }

  /**
   * Gets a tracker.
   * 
   * @return a tracker, not null
   */
  public static PartTracker get() {
    return new PartTracker();
  }

  /**
   * Gets a tracker with listeners attached.
   * 
   * @param listeners the listeners to be attached
   * @return a tracker, not null
   * @throws NullPointerException if listeners contain null
   */
  public static PartTracker withListeners(IPartFocusListener... listeners) {
    return new PartTracker(listeners);
  }

  private final IPartListener partListener = new PartListener() {
    @Override public void partActivated(IWorkbenchPart part) {
      super.partActivated(part);
      onPartFocused(part);
    }

    @Override public void partDeactivated(IWorkbenchPart part) {
      super.partDeactivated(part);
      onPartUnfocused(part);
    }
  };

  private final IWindowListener windowListener = new IWindowListener() {
    @Override public void windowActivated(IWorkbenchWindow window) {
      IWorkbenchPart part = window.getPartService().getActivePart();
      if (part != null) {
        onPartFocused(part);
      }
    }

    @Override public void windowClosed(IWorkbenchWindow window) {
      window.getPartService().removePartListener(partListener);
    }

    @Override public void windowDeactivated(IWorkbenchWindow window) {
      IWorkbenchPart part = window.getPartService().getActivePart();
      if (part != null) {
        onPartUnfocused(part);
      }
    }

    @Override public void windowOpened(IWorkbenchWindow window) {
      window.getPartService().addPartListener(partListener);
    }
  };

  private final Set<IPartFocusListener> listeners;

  private PartTracker(IPartFocusListener... listeners) {
    check(listeners);
    this.listeners = new CopyOnWriteArraySet<IPartFocusListener>(
        newArrayList(listeners));
  }

  private void check(IPartFocusListener... listeners) {
    if (listeners.length > 0) {
      String errorMessage = "null contained in " + Arrays.toString(listeners);
      for (IPartFocusListener listener : listeners) {
        checkNotNull(listener, errorMessage);
      }
    }
  }

  /**
   * Adds a listener to listen to part focus events. Has no affect if an
   * identical listener has already been added.
   * 
   * @param listener the listener to add, not null
   * @throws NullPointerException if listener is null
   */
  public void addListener(IPartFocusListener listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  /**
   * Removes the given listener from listening to part focus events.
   * 
   * @param listener the listener to remove, not null
   * @throws NullPointerException if listener is null
   */
  public void removeListener(IPartFocusListener listener) {
    listeners.remove(checkNotNull(listener, "listener"));
  }

  @Override protected void onDisable() {
    getWorkbench().removeWindowListener(windowListener);
    for (IPartService service : getPartServices()) {
      service.removePartListener(partListener);
    }
  }

  @Override protected void onEnable() {
    getWorkbench().addWindowListener(windowListener);
    for (IPartService service : getPartServices()) {
      service.addPartListener(partListener);
    }

    IWorkbenchPart part = getFocusedPart();
    if (part != null) {
      onPartFocused(part);
    }
  }

  private void onPartFocused(IWorkbenchPart part) {
    for (IPartFocusListener listener : listeners) {
      listener.onPartFocused(part);
    }
  }

  private void onPartUnfocused(IWorkbenchPart part) {
    for (IPartFocusListener listener : listeners) {
      listener.onPartUnfocused(part);
    }
  }
}
