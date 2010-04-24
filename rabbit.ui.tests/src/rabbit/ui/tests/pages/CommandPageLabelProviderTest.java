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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.CommandLabelProvider;
import rabbit.ui.internal.pages.CommandPageContentProvider;
import rabbit.ui.internal.pages.CommandPageLabelProvider;
import rabbit.ui.internal.pages.DateLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test for {@link CommandPageLabelProvider}
 */
@SuppressWarnings("restriction")
public class CommandPageLabelProviderTest {

  private static Shell shell;
  private static CommandPageLabelProvider labelProvider;
  private static CommandPageContentProvider contents;
  private static CommandLabelProvider commandLabels;
  private static DateLabelProvider dateLabels;

  private static TreeNode dateNode;
  private static TreeNode definedNode;
  private static TreeNode undefinedNode;

  @AfterClass
  public static void afterClass() {
    labelProvider.dispose();
    commandLabels.dispose();
    dateLabels.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    contents = new CommandPageContentProvider(viewer);
    labelProvider = new CommandPageLabelProvider(contents);
    Command defined = getCommandService().getDefinedCommands()[0];
    Command undefined = getCommandService().getCommand(System.currentTimeMillis() + "");

    viewer.setContentProvider(contents);
    viewer.setLabelProvider(labelProvider);

    dateNode = new TreeNode(new LocalDate());
    definedNode = new TreeNode(defined);
    undefinedNode = new TreeNode(undefined);
    
    dateLabels = new DateLabelProvider();
    commandLabels = new CommandLabelProvider();
  }

  private static ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_contentProviderNull() {
    new CommandPageLabelProvider(null);
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
    assertEquals(commandLabels.getText(definedNode.getValue()), labelProvider.getText(definedNode));
    assertEquals(commandLabels.getText(undefinedNode.getValue()), labelProvider.getText(undefinedNode));
    assertEquals(dateLabels.getText(dateNode.getValue()), labelProvider.getText(dateNode));
  }

  @Test
  public void testGetColumnText_0() throws Exception {
    assertEquals(commandLabels.getText(definedNode.getValue()), labelProvider.getColumnText(definedNode, 0));
    assertEquals(commandLabels.getText(undefinedNode.getValue()), labelProvider.getColumnText(undefinedNode, 0));
    assertEquals(dateLabels.getText(dateNode.getValue()), labelProvider.getColumnText(dateNode, 0));
  }

  @Test
  public void testGetColumnText_2() throws Exception {
    Command defined = (Command) definedNode.getValue();
    Command undefined = (Command) undefinedNode.getValue();
    
    CommandDataDescriptor d1;
    CommandDataDescriptor d2;
    LocalDate date = new LocalDate();
    d1 = new CommandDataDescriptor(date, 11, defined.getId());
    d2 = new CommandDataDescriptor(date, 102, undefined.getId());
    contents.getViewer().setInput(Arrays.asList(d1, d2));

    contents.setSelectedCategories(Category.COMMAND);
    contents.setPaintCategory(Category.COMMAND);
    TreeNode root = contents.getRoot();
    TreeNode[] commandNodes = root.getChildren();
    assertEquals(2, commandNodes.length);
    assertEquals(d1.getValue() + "", labelProvider.getColumnText(commandNodes[0], 2));
    assertEquals(d2.getValue() + "", labelProvider.getColumnText(commandNodes[1], 2));
    
    contents.setSelectedCategories(Category.DATE);
    contents.setPaintCategory(Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode dateNode = root.getChildren()[0];
    assertEquals(d1.getValue() + d2.getValue() + "", labelProvider.getColumnText(dateNode, 2));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    Command defined = (Command) definedNode.getValue();
    Command undefined = (Command) undefinedNode.getValue();
    assertEquals(defined.getDescription(), labelProvider.getColumnText(defined, 1));
    assertNull(labelProvider.getColumnText(undefined, 1));
  }

  @Test
  public void testGetForeground() {
    assertNull(labelProvider.getForeground(definedNode));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        labelProvider.getForeground(undefinedNode));
  }
}
