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
package rabbit.ui.tests.actions;

import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;

import static org.junit.Assert.*;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.junit.Test;

/**
 * @see GroupByAction
 */
@SuppressWarnings("restriction")
public class GroupByActionTest {

  /** Action for testing. */
  private IAction action = new Action("Abc") {
  };

  /** Provider for testing. */
  private ICategoryProvider categoryProvider = new ICategoryProvider() {
    @Override
    public void setSelectedCategories(ICategory... categories) {
    }

    @Override
    public ICategory[] getUnselectedCategories() {
      return new ICategory[0];
    }

    @Override
    public ICategory[] getSelectedCategories() {
      return new ICategory[0];
    }
  };

  @Test(expected = NullPointerException.class)
  public void testConstructor_categoryProvider_null() {
    new GroupByAction(null, action);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_defaultAction_null() {
    new GroupByAction(categoryProvider, null);
  }

  @Test
  public void testGetText() {
    assertEquals("Group by " + action.getText(), new GroupByAction(
        categoryProvider, action).getText());
  }
}
