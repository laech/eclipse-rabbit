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

import rabbit.ui.internal.pages.AbstractFilteredTreePage;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.ResourcePage;
import rabbit.ui.internal.pages.ResourcePageContentProvider;
import rabbit.ui.internal.util.ICategory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see ResourcePage
 */
public class ResourcePageTest extends AbstractFilteredTreePageTest {

  @Test
  public void testSaveState_selectedCategories() throws Exception {
    ResourcePageContentProvider contents = (ResourcePageContentProvider) page
        .getViewer().getContentProvider();
    
    ICategory[] categories = new ICategory[] { Category.DATE, Category.FILE };
    contents.setSelectedCategories(categories);
    saveState(page);
    
    contents.setSelectedCategories(new ICategory[] { Category.FOLDER });
    restoreState(page);
    assertArrayEquals(categories, contents.getSelectedCategories());
  }
  
  @Test
  public void testSaveState_paintCategory() throws Exception {
    ResourcePageContentProvider contents = (ResourcePageContentProvider) page
        .getViewer().getContentProvider();
    
    Category paintCategory = Category.FILE;
    contents.setPaintCategory(paintCategory);
    saveState(page);
    
    contents.setPaintCategory(Category.DATE);
    restoreState(page);
    assertEquals(paintCategory, contents.getPaintCategory());
  }

  @Override
  protected AbstractFilteredTreePage createPage() {
    return new ResourcePage();
  }
}
