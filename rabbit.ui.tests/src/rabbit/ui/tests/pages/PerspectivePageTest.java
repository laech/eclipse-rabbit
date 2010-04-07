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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.pages.PerspectivePage;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test for {@link PerspectivePage}
 */
@SuppressWarnings("restriction")
public class PerspectivePageTest extends AbstractTreeViewerPageTest {

  @Test
  public void testGetValue() {
    PerspectiveDataDescriptor des1 = new PerspectiveDataDescriptor(
        new LocalDate(), 19834, "abc");
    PerspectiveDataDescriptor des2 = new PerspectiveDataDescriptor(des1
        .getDate().minusDays(1), 14, des1.getPerspectiveId());

    page.getViewer().setInput(Arrays.asList(des1, des2));
    assertEquals(des1.getValue(), page.getValue(des1));
    assertEquals(des2.getValue(), page.getValue(des2));
    assertEquals(des1.getValue() + des2.getValue(), page
        .getValue(new UndefinedPerspectiveDescriptor(des1.getPerspectiveId())));
  }

  @Override
  protected PerspectivePage createPage() {
    return new PerspectivePage();
  }

}
