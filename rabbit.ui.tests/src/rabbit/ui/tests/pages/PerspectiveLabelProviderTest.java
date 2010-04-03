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

import rabbit.ui.MillisConverter;
import rabbit.ui.internal.pages.PerspectivePage;
import rabbit.ui.internal.pages.PerspectivePageLabelProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class PerspectiveLabelProviderTest {

  private static Shell shell;
  private static PerspectivePage page;
  private static PerspectivePageLabelProvider provider;
  private static IPerspectiveDescriptor definedPerspective;
  private static IPerspectiveDescriptor undefinedPerspective;

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new PerspectivePage();
    page.createContents(shell);
    provider = new PerspectivePageLabelProvider(page);
    definedPerspective = PlatformUI.getWorkbench().getPerspectiveRegistry()
        .getPerspectives()[0];
    undefinedPerspective = new UndefinedPerspectiveDescriptor("abc.def.g");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testDispose() throws Exception {
    Field internalProviderField = PerspectivePageLabelProvider.class
        .getDeclaredField("provider");
    internalProviderField.setAccessible(true);

    PerspectiveLabelProvider internalProvider = (PerspectiveLabelProvider) internalProviderField
        .get(provider);
    Field imageField = internalProvider.getClass().getDeclaredField(
        "imageCache");
    imageField.setAccessible(true);
    Map images = (Map) imageField.get(internalProvider);
    assertFalse(images.isEmpty());
    for (Object img : images.values()) {
      assertFalse(((Image) img).isDisposed());
    }

    provider.dispose();
    for (Object img : images.values()) {
      assertTrue(((Image) img).isDisposed());
    }
  }

  @Test
  public void testGetBackground() {
    assertNull(provider.getBackground(definedPerspective));
    assertNull(provider.getBackground(undefinedPerspective));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(provider.getColumnImage(definedPerspective, 0));
    assertNotNull(provider.getColumnImage(undefinedPerspective, 0));

    assertNull(provider.getColumnImage(definedPerspective, 1));
    assertNull(provider.getColumnImage(undefinedPerspective, 1));
  }

  @Test
  public void testGetColumnText() throws Exception {
    Map<IPerspectiveDescriptor, Long> data = PerspectivePageTest.getData(page);

    long definedValue = 18340;
    data.put(definedPerspective, definedValue);
    assertEquals(definedPerspective.getLabel(), provider.getColumnText(
        definedPerspective, 0));
    assertEquals(MillisConverter.toDefaultString(definedValue), provider
        .getColumnText(definedPerspective, 1));

    long undefinedValue = 18736392l;
    data.put(undefinedPerspective, undefinedValue);
    assertEquals(undefinedPerspective.getLabel(), provider.getColumnText(
        undefinedPerspective, 0));
    assertEquals(MillisConverter.toDefaultString(undefinedValue), provider
        .getColumnText(undefinedPerspective, 1));
  }

  @Test
  public void testGetForeground() {
    assertNull(provider.getForeground(definedPerspective));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(undefinedPerspective));
  }
}
