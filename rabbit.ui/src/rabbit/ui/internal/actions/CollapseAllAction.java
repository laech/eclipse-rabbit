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
package rabbit.ui.internal.actions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Collapses all elements in a tree viewer.
 */
public class CollapseAllAction extends Action {

  private TreeViewer viewer;

  /**
   * Constructor.
   * 
   * @param viewer The viewer to perform action on.
   */
  public CollapseAllAction(TreeViewer viewer) {
    super("Collapse All", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
    checkNotNull(viewer);
    this.viewer = viewer;
  }

  @Override
  public void run() {
    super.run();
    viewer.collapseAll();
  }
}
