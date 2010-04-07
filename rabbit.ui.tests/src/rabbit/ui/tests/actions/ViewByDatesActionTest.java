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

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.ViewByDatesAction;
import rabbit.ui.internal.pages.AbstractDateCategoryContentProvider;
import rabbit.ui.internal.pages.PartPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.jface.action.IAction;
import org.junit.Test;

/**
 * @see ViewByDatesAction
 */
@SuppressWarnings("restriction")
public class ViewByDatesActionTest {

  private final ViewByDatesAction action;
  private final AbstractDateCategoryContentProvider provider;

  public ViewByDatesActionTest() {
    provider = newContentProvider(false);
    action = new ViewByDatesAction(provider);
  }

  @Test
  public void testDefaultState() {
    AbstractDateCategoryContentProvider content = newContentProvider(false);
    assertSame(new ViewByDatesAction(content).isChecked(), content
        .isDisplayingByDate());

    content = newContentProvider(true);
    assertSame(new ViewByDatesAction(content).isChecked(), content
        .isDisplayingByDate());
  }

  @Test
  public void testImageDescriptor() {
    assertSame(SharedImages.TIME_HIERARCHY, action.getImageDescriptor());
  }

  @Test
  public void testRun() {
    action.run();
    assertEquals(action.isChecked(), provider.isDisplayingByDate());
    action.run();
    assertEquals(action.isChecked(), provider.isDisplayingByDate());
  }

  @Test
  public void testStyle() {
    assertSame(IAction.AS_CHECK_BOX, action.getStyle());
  }

  @Test
  public void testText() {
    assertEquals("View by Dates", action.getText());
  }

  private AbstractDateCategoryContentProvider newContentProvider(
      boolean displayByDates) {
    return new AbstractDateCategoryContentProvider(new PartPage(),
        displayByDates) {

      @Override
      public Object[] getChildren(Object parentElement) {
        return null;
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return null;
      }

      @Override
      public boolean hasChildren(Object element) {
        return false;
      }

      @Override
      protected void updatePageMaxValue() {
      }
    };
  }

}
