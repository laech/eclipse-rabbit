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

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.pages.PartPage;
import rabbit.ui.internal.pages.PartPageContentProvider;
import rabbit.ui.internal.pages.PartPageLabelProvider;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test for {@link PartPageLabelProvider}
 */
@SuppressWarnings("restriction")
public class PartPageLabelProviderTest {

  private static Shell shell;
  private static PartPage page;
  private static PartPageLabelProvider labels;
  private static PartPageContentProvider contents;

  /** A defined workbench part. */
  private static IWorkbenchPartDescriptor defined;

  /** An undefined workbench part (not exists) */
  private static IWorkbenchPartDescriptor undefined;

  @AfterClass
  public static void afterClass() {
    labels.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new PartPage();
    contents = new PartPageContentProvider(page, true);
    labels = new PartPageLabelProvider(contents);
    defined = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
    undefined = new UndefinedWorkbenchPartDescriptor("abc.def.g");

    page.createContents(shell);
    page.getViewer().setContentProvider(contents);
    page.getViewer().setLabelProvider(labels);
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

    PartDataDescriptor des;
    des = new PartDataDescriptor(new LocalDate(), 1, defined.getId());
    assertEquals(defined.getLabel(), labels.getColumnText(des, 0));

    des = new PartDataDescriptor(new LocalDate(), 1, undefined.getId());
    assertEquals(undefined.getLabel(), labels.getColumnText(undefined, 0));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    PartDataDescriptor d1;
    PartDataDescriptor d2;
    d1 = new PartDataDescriptor(new LocalDate(), 111, defined.getId());
    d2 = new PartDataDescriptor(new LocalDate(), 101, d1.getPartId());
    assertEquals(format(d1.getValue()), labels.getColumnText(d1, 1));
    assertEquals(format(d2.getValue()), labels.getColumnText(d2, 1));
    assertEquals(format(d1.getValue() + d2.getValue()), labels
        .getColumnText(defined, 1));

    d1 = new PartDataDescriptor(new LocalDate(), 222, undefined.getId());
    d2 = new PartDataDescriptor(new LocalDate(), 999, d1.getPartId());
    page.getViewer().setInput(Arrays.asList(d1, d2));
    assertEquals(format(d1.getValue()), labels.getColumnText(d1, 1));
    assertEquals(format(d2.getValue()), labels.getColumnText(d2, 1));
    assertEquals(format(d1.getValue() + d2.getValue()), labels
        .getColumnText(undefined, 1));
  }

  @Test
  public void testGetForeground() {
    assertNull(labels.getForeground(defined));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        labels.getForeground(undefined));
    
    PartDataDescriptor d1;
    PartDataDescriptor d2;
    d1 = new PartDataDescriptor(new LocalDate(), 1, defined.getId());
    d2 = new PartDataDescriptor(new LocalDate(), 1, undefined.getId());
    
    page.getViewer().setInput(Arrays.asList(d1, d2));
    assertNull(labels.getForeground(d1));
    assertNotNull(labels.getForeground(d2));
  }

}
