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

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static rabbit.tracking.internal.workbench.util.WorkbenchUtil.getPartServices;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import rabbit.tracking.AbstractUserTracker;
import rabbit.tracking.internal.workbench.util.PartListener;
import rabbit.tracking.internal.workbench.util.WorkbenchUtil;

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
 * {@link #onPartFocused(IWorkbenchPart)} will be called when a part becomes
 * focused, and when a new part becomes focused,
 * {@link #onPartUnfocused(IWorkbenchPart)} will be called with the old part
 * before the new part is called with {@link #onPartFocused(IWorkbenchPart)}.
 * <p/>
 * When this tracker is enabled, if there is a currently focused part,
 * {@link #onPartFocused(IWorkbenchPart)} will be called immediately.
 * 
 * @since 2.0
 */
public abstract class AbstractPartTracker extends AbstractUserTracker {

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

  private final IWindowListener winListener = new IWindowListener() {
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

  public AbstractPartTracker() {
    super();
  }

  @Override protected void onDisable() {
    super.onDisable();
    getWorkbench().removeWindowListener(winListener);
    for (IPartService service : getPartServices()) {
      service.removePartListener(partListener);
    }
  }

  @Override protected void onEnable() {
    super.onEnable();
    getWorkbench().addWindowListener(winListener);
    for (IPartService service : getPartServices()) {
      service.addPartListener(partListener);
    }

    IWorkbenchPart part = getFocusedPart();
    if (part != null) {
      onPartFocused(part);
    }
  }

  /**
   * Gets the currently focused part.
   * 
   * @return the focused part, or null if no part is focused
   */
  protected IWorkbenchPart getFocusedPart() {
    return WorkbenchUtil.getFocusedPart();
  }

  /**
   * Called when a workbench part became focused.
   * 
   * @param part the workbench part in focus, not null
   */
  protected abstract void onPartFocused(IWorkbenchPart part);

  /**
   * Called when the previously focused workbench part no longer has the focus.
   * 
   * @param part the part that lost focus, not null
   */
  protected abstract void onPartUnfocused(IWorkbenchPart part);

}
