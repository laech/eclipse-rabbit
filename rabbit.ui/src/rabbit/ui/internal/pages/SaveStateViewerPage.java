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
package rabbit.ui.internal.pages;

import rabbit.ui.IPage;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.IVisualProvider;
import rabbit.ui.internal.util.TreePathValueProvider;

import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;

import java.util.List;

// TODO
public abstract class SaveStateViewerPage implements IPage {

  @Override
  public void onRestoreState(IMemento memento) {
    String id = getClass().getSimpleName();

    StateHelper helper = StateHelper.of(memento, id);
    helper.restoreColumnWidths(getColumns());

    Category visual = helper.retrieveSavedVisualCategory();
    if (visual != null) {
      setVisualCategory(visual);
    }

    List<Category> selected = helper.retrieveSavedCategories();
    if (selected != null) {
      setSelectedCategories(selected);
    }
  }

  @Override
  public void onSaveState(IMemento memento) {
    String id = getClass().getSimpleName();
    StateHelper
        .of(memento, id)
        .saveColumnWidths(getColumns())
        .saveVisualCategory(getVisualCategory())
        .saveCategories(getSelectedCategories());
  }

  protected abstract TreeColumn[] getColumns();

  /**
   * @return the current selected categories.
   * @see ICategoryProvider#getSelected()
   */
  protected abstract Category[] getSelectedCategories();

  /**
   * @return the current visual category.
   * @see IVisualProvider#getVisualCategory()
   */
  protected abstract Category getVisualCategory();

  /**
   * @param categories the categories to set.
   * @see ICategoryProvider#setSelected(ICategory...)
   */
  protected abstract void setSelectedCategories(List<Category> categories);

  /**
   * @param category the category to set.
   * @see TreePathValueProvider#setVisualCategory(ICategory)
   */
  protected abstract void setVisualCategory(Category category);
}
