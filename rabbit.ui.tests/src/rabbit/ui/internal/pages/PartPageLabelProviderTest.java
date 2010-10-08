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

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link PartPageLabelProvider}
 */
public class PartPageLabelProviderTest {

  private static Shell shell;
  private static PartPageLabelProvider labelProvider;
  private static PartPageContentProvider contents;
  private static WorkbenchPartLabelProvider partLabels;
  private static DateLabelProvider dateLabels;

  private static TreeNode dateNode;
  private static TreeNode definedNode;
  private static TreeNode undefinedNode;

  @AfterClass
  public static void afterClass() {
    labelProvider.dispose();
    partLabels.dispose();
    dateLabels.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    contents = new PartPageContentProvider(viewer);
    labelProvider = new PartPageLabelProvider(contents);
    viewer.setContentProvider(contents);
    viewer.setLabelProvider(labelProvider);

    IWorkbenchPartDescriptor defined = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
    IWorkbenchPartDescriptor undefined = new UndefinedWorkbenchPartDescriptor(
        System.currentTimeMillis() + "");

    dateNode = new TreeNode(new LocalDate());
    definedNode = new TreeNode(defined);
    undefinedNode = new TreeNode(undefined);

    dateLabels = new DateLabelProvider();
    partLabels = new WorkbenchPartLabelProvider();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_contentProviderNull() {
    new PartPageLabelProvider(null);
  }

  @Test
  public void testGetBackground() {
    assertNull(labelProvider.getBackground(definedNode));
    assertNull(labelProvider.getBackground(undefinedNode));
    assertNull(labelProvider.getBackground(dateNode));
  }

  @Test
  public void testGetImage() {
    assertNotNull(labelProvider.getImage(dateNode));
    assertNotNull(labelProvider.getImage(definedNode));
    assertNotNull(labelProvider.getImage(undefinedNode));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(labelProvider.getColumnImage(dateNode, 0));
    assertNotNull(labelProvider.getColumnImage(definedNode, 0));
    assertNotNull(labelProvider.getColumnImage(undefinedNode, 0));
  }

  @Test
  public void testGetText() {
    assertEquals(partLabels.getText(definedNode.getValue()),
        labelProvider.getText(definedNode));
    assertEquals(partLabels.getText(undefinedNode.getValue()),
        labelProvider.getText(undefinedNode));
    assertEquals(dateLabels.getText(dateNode.getValue()),
        labelProvider.getText(dateNode));
  }

  @Test
  public void testGetColumnText_0() throws Exception {
    assertEquals(partLabels.getText(definedNode.getValue()),
        labelProvider.getColumnText(definedNode, 0));
    assertEquals(partLabels.getText(undefinedNode.getValue()),
        labelProvider.getColumnText(undefinedNode, 0));
    assertEquals(dateLabels.getText(dateNode.getValue()),
        labelProvider.getColumnText(dateNode, 0));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    IWorkbenchPartDescriptor defined = (IWorkbenchPartDescriptor) definedNode.getValue();
    IWorkbenchPartDescriptor undefined = (IWorkbenchPartDescriptor) undefinedNode.getValue();

    PartDataDescriptor d1;
    PartDataDescriptor d2;
    LocalDate date = new LocalDate();
    d1 = new PartDataDescriptor(date, new Duration(11), defined.getId());
    d2 = new PartDataDescriptor(date, new Duration(12), undefined.getId());
    contents.getViewer().setInput(PartPageContentProviderTest.newInput(d1, d2));

    contents.setSelectedCategories(Category.WORKBENCH_TOOL);
    contents.setPaintCategory(Category.WORKBENCH_TOOL);
    TreeNode root = contents.getRoot();
    TreeNode[] partNodes = root.getChildren();
    assertEquals(2, partNodes.length);
    assertEquals(format(d1.getDuration().getMillis()),
        labelProvider.getColumnText(partNodes[0], 1));
    assertEquals(format(d2.getDuration().getMillis()),
        labelProvider.getColumnText(partNodes[1], 1));

    contents.setSelectedCategories(Category.DATE);
    contents.setPaintCategory(Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode dateNode = root.getChildren()[0];
    assertEquals(format(d1.getDuration().getMillis()
        + d2.getDuration().getMillis()), labelProvider.getColumnText(dateNode,
        1));
  }

  @Test
  public void testGetForeground() {
    assertNull(labelProvider.getForeground(definedNode));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        labelProvider.getForeground(undefinedNode));
  }

}
