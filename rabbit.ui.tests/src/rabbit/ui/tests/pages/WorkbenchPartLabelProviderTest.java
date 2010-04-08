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

import rabbit.ui.internal.pages.WorkbenchPartLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Test for {@link WorkbenchPartLabelProvider}
 */
@SuppressWarnings("restriction")
public class WorkbenchPartLabelProviderTest {

  private static WorkbenchPartLabelProvider provider;
  private static IWorkbenchPartDescriptor part;

  @AfterClass
  public static void afterClass() {
    provider.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    provider = new WorkbenchPartLabelProvider();
    part = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testDispose() throws Exception {
    provider.getImage(part); // Call once to create an image.
    Field field = WorkbenchPartLabelProvider.class.getDeclaredField("images");
    field.setAccessible(true);
    Map<String, Image> images = (Map<String, Image>) field.get(provider);
    assertFalse(images.isEmpty());
    for (Image img : images.values()) {
      if (img != null) {
        assertFalse(img.isDisposed());
      }
    }

    provider.dispose();
    for (Image img : images.values()) {
      if (img != null) {
        assertTrue(img.isDisposed());
      }
    }
  }

  @Test
  public void testGetImage() {
    assertNotNull(provider.getImage(part));
  }

  @Test
  public void testGetText() throws Exception {
    assertEquals(part.getLabel(), provider.getText(part));
  }

}
