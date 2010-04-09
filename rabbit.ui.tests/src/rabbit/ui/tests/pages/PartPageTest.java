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
package rabbit.ui.tests.pages;

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.pages.AbstractTreeViewerPage;
import rabbit.ui.internal.pages.PartPage;
import rabbit.ui.internal.pages.PartPageContentProvider;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test for {@link PartPage}
 */
@SuppressWarnings("restriction")
public class PartPageTest extends AbstractTreeViewerPageTest {
  
  @Test
  public void testGetValue() throws Exception {
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 19834, "abc");
    PartDataDescriptor des2 = new PartDataDescriptor(des1
        .getDate().minusDays(1), 14, des1.getPartId());

    page.getViewer().setInput(Arrays.asList(des1, des2));
    assertEquals(des1.getValue(), page.getValue(des1));
    assertEquals(des2.getValue(), page.getValue(des2));
    assertEquals(des1.getValue() + des2.getValue(), page
        .getValue(new UndefinedWorkbenchPartDescriptor(des1.getPartId())));
  }
  
  @Test
  public void testUpdate() throws Exception {
    PartPageContentProvider cp;
    cp = (PartPageContentProvider) page.getViewer().getContentProvider();
    cp.setDisplayByDate(true);
    
    PartDataDescriptor des1;
    PartDataDescriptor des2;
    des1 = new PartDataDescriptor(new LocalDate(), 101, "123");
    des2 = new PartDataDescriptor(new LocalDate(), 9823, des1.getPartId());
    page.getViewer().setInput(Arrays.asList(des1, des2));
    
    assertEquals(des2.getValue(), page.getMaxValue());
    
    cp.setDisplayByDate(false);
    assertEquals(des1.getValue() + des2.getValue(), page.getMaxValue());
  }

  @Override
  protected AbstractTreeViewerPage createPage() {
    return new PartPage();
  }
}
