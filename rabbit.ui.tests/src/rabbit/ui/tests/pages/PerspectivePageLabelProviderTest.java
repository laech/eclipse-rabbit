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

import static rabbit.ui.MillisConverter.toDefaultString;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.pages.PerspectivePage;
import rabbit.ui.internal.pages.PerspectivePageContentProvider;
import rabbit.ui.internal.pages.PerspectivePageLabelProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see PerspectivePageLabelProvider
 */
@SuppressWarnings("restriction")
public class PerspectivePageLabelProviderTest {

  private static final Shell shell;
  private static final PerspectivePage page;
  private static final PerspectivePageLabelProvider labels;
  private static final PerspectivePageContentProvider contents;

  /** A defined perspective. */
  private static final IPerspectiveDescriptor defined;

  /** An undefined perspective (not exists) */
  private static final IPerspectiveDescriptor undefined;

  static {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new PerspectivePage();
    contents = new PerspectivePageContentProvider(page, true);
    labels = new PerspectivePageLabelProvider(contents);

    page.createContents(shell);
    page.getViewer().setContentProvider(contents);
    page.getViewer().setLabelProvider(
        new PerspectivePageLabelProvider(contents));

    undefined = new UndefinedPerspectiveDescriptor(System.nanoTime() + "");
    defined = PlatformUI.getWorkbench().getPerspectiveRegistry()
        .getPerspectives()[0];
  }

  @AfterClass
  public static void afterClass() {
    labels.dispose();
    shell.dispose();
  }

  @Test
  public void testGetBackground() {
    assertNull(labels.getBackground(defined));
    assertNull(labels.getBackground(undefined));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(labels.getColumnImage(defined, 0));
    assertNotNull(labels.getColumnImage(undefined, 0));
  }

  @Test
  public void testGetColumnText_0() throws Exception {
    assertEquals(defined.getLabel(), labels.getColumnText(defined, 0));
    assertEquals(undefined.getLabel(), labels.getColumnText(undefined, 0));

    PerspectiveDataDescriptor des;
    des = new PerspectiveDataDescriptor(new LocalDate(), 1, defined.getId());
    assertEquals(defined.getLabel(), labels.getColumnText(des, 0));

    des = new PerspectiveDataDescriptor(new LocalDate(), 1, undefined.getId());
    assertEquals(undefined.getLabel(), labels.getColumnText(undefined, 0));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    PerspectiveDataDescriptor d1;
    PerspectiveDataDescriptor d2;
    d1 = new PerspectiveDataDescriptor(new LocalDate(), 111, defined.getId());
    d2 = new PerspectiveDataDescriptor(new LocalDate(), 101, defined.getId());
    assertEquals(toDefaultString(d1.getValue()), labels.getColumnText(d1, 1));
    assertEquals(toDefaultString(d2.getValue()), labels.getColumnText(d2, 1));
    assertEquals(toDefaultString(d1.getValue() + d2.getValue()), labels
        .getColumnText(defined, 1));

    d1 = new PerspectiveDataDescriptor(new LocalDate(), 222, undefined.getId());
    d2 = new PerspectiveDataDescriptor(new LocalDate(), 999, undefined.getId());
    page.getViewer().setInput(Arrays.asList(d1, d2));
    assertEquals(toDefaultString(d1.getValue()), labels.getColumnText(d1, 1));
    assertEquals(toDefaultString(d2.getValue()), labels.getColumnText(d2, 1));
    assertEquals(toDefaultString(d1.getValue() + d2.getValue()), labels
        .getColumnText(undefined, 1));
  }

  @Test
  public void testGetForeground() {
    assertNull(labels.getForeground(defined));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        labels.getForeground(undefined));

    PerspectiveDataDescriptor d1;
    PerspectiveDataDescriptor d2;
    d1 = new PerspectiveDataDescriptor(new LocalDate(), 1, defined.getId());
    d2 = new PerspectiveDataDescriptor(new LocalDate(), 1, undefined.getId());
    
    page.getViewer().setInput(Arrays.asList(d1, d2));
    assertNull(labels.getForeground(d1));
    assertNotNull(labels.getForeground(d2));
  }
}
