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

import org.eclipse.ui.IWorkbenchPart;

/**
 * Listener to listen to part focus events. A part is consider focused if all of
 * the followings are true:
 * <ul>
 * <li>it's the active part in the parent window</li>
 * <li>its parent window is focused</li>
 * </ul>
 * therefore there will be at most one focused part at any time regardless of
 * how many workbench windows are opened.
 * 
 * @since 2.0
 */
public interface IPartFocusListener {

  /**
   * Called when a workbench part became focused.
   * 
   * @param part the workbench part in focus, not null
   */
  void onPartFocused(IWorkbenchPart part);

  /**
   * Called when the previously focused workbench part no longer has the focus.
   * 
   * @param part the part that lost focus, not null
   */
  void onPartUnfocused(IWorkbenchPart part);
}