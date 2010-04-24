/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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
package rabbit.ui.internal.actions;

import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * Action to show or hide the filter control of a {@link FilteredTree}
 */
public class ShowHideFilterControlAction extends Action {

  private final FilteredTree fTree;

  /**
   * Creates an action for the given tree.
   * 
   * @param tree The target of this action.
   */
  public ShowHideFilterControlAction(FilteredTree tree) {
    super("Search", IAction.AS_CHECK_BOX);
    setImageDescriptor(SharedImages.SEARCH);
    setChecked(tree.getFilterControl().getParent().isVisible());
    fTree = tree;
  }

  @Override
  public void run() {
    GridData data = (GridData) fTree.getFilterControl().getParent()
        .getLayoutData();
    GridLayout layout = (GridLayout) fTree.getLayout();

    if (data.heightHint == 0) {
      data.heightHint = SWT.DEFAULT;
      fTree.getFilterControl().getParent().setVisible(true);
      fTree.getFilterControl().setFocus();
    } else {
      data.heightHint = 0;
      layout.verticalSpacing = 0;
      fTree.getFilterControl().getParent().setVisible(false);
    }

    fTree.layout();
    setChecked(fTree.getFilterControl().getParent().isVisible());
  }
}
