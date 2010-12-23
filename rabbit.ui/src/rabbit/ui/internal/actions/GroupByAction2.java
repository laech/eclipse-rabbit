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
import rabbit.ui.internal.util.ICategoryProvider2;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Action to contain a list of "Group by" actions and an extra action at the end
 * to show the group by dialog. The text of this action is the text of the
 * default action prefixed with "Group by ".
 */
public class GroupByAction2 extends DropDownAction {

  private ICategoryProvider2 categoryProvider;
  
  /**
   * Constructor. This constructor uses the first action in the arguments as the default, that is,
   * the default text/image/action will be taken from the first action.
   * @param actions the list of actions, length must be greater than 1.
   */
  public GroupByAction2(ICategoryProvider2 categoryProvider, IAction... actions) {
    super(actions);
    this.categoryProvider = checkNotNull(categoryProvider);
    setText("Group by " + getText());
    setImageDescriptor(SharedImages.HIERARCHY);
  }

  /**
   * Gets the category content provider.
   * 
   * @return The provider.
   */
  public ICategoryProvider2 getCategoryProvider() {
    return categoryProvider;
  }

  @Override
  public Menu getMenu(Control parent) {
    if (menu == null || menu.isDisposed()) {
      menu = super.getMenu(parent);

      if (getMenuItemActions().length > 0)
        new MenuItem(menu, SWT.SEPARATOR);

      IAction showAction = new ShowGroupByDialogAction2(categoryProvider);
      new ActionContributionItem(showAction).fill(menu, -1);
    }
    return menu;
  }
}
