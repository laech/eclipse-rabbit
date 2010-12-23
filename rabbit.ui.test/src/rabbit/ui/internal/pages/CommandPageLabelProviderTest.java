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

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.Path;
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

/**
 * Test for {@link CommandPageLabelProvider}
 */
public class CommandPageLabelProviderTest {

  private static Shell shell;
  private static CommandPageLabelProvider labelProvider;
  private static CommandPageContentProvider contents;
  private static CommandLabelProvider commandLabels;
  private static DateLabelProvider dateLabels;
  private static WorkspaceStorageLabelProvider workspaceLabels;

  private static TreeNode dateNode;
  private static TreeNode definedNode;
  private static TreeNode undefinedNode;
  private static TreeNode workspaceNode;

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
    WorkspaceStorage ws = new WorkspaceStorage(Path.fromOSString("/"), Path.fromOSString("/"));

    viewer.setContentProvider(contents);
    viewer.setLabelProvider(labelProvider);

    dateNode = new TreeNode(new LocalDate());
    definedNode = new TreeNode(defined);
    undefinedNode = new TreeNode(undefined);
    workspaceNode = new TreeNode(ws);

    dateLabels = new DateLabelProvider();
    commandLabels = new CommandLabelProvider();
    workspaceLabels = new WorkspaceStorageLabelProvider();
  }

  private static ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIsTryToConstructWithoutAValueProvider() {
    new CommandPageLabelProvider(null);
  }

  @Test
  public void getBackground() {
    assertNull(labelProvider.getBackground(definedNode));
    assertNull(labelProvider.getBackground(undefinedNode));
    assertNull(labelProvider.getBackground(dateNode));
    assertNull(labelProvider.getBackground(workspaceNode));
  }

  @Test
  public void getImage() {
    assertNotNull(labelProvider.getImage(dateNode));
    assertNotNull(labelProvider.getImage(definedNode));
    assertNotNull(labelProvider.getImage(workspaceNode));
    assertNull(labelProvider.getImage(undefinedNode));
  }

  @Test
  public void getColumnImage() {
    assertNotNull(labelProvider.getColumnImage(dateNode, 0));
    assertNotNull(labelProvider.getColumnImage(definedNode, 0));
    assertNotNull(labelProvider.getColumnImage(workspaceNode, 0));

    assertNull(labelProvider.getColumnImage(undefinedNode, 0));
  }

  @Test
  public void getText() {
    assertEquals(commandLabels.getText(definedNode.getValue()), labelProvider.getText(definedNode));
    assertEquals(commandLabels.getText(undefinedNode.getValue()), labelProvider.getText(undefinedNode));
    assertEquals(dateLabels.getText(dateNode.getValue()), labelProvider.getText(dateNode));
    assertEquals(workspaceLabels.getText(workspaceNode.getValue()), labelProvider.getText(workspaceNode));
  }

  @Test
  public void getColumnText_0() throws Exception {
    assertEquals(commandLabels.getText(definedNode.getValue()), labelProvider.getColumnText(definedNode, 0));
    assertEquals(commandLabels.getText(undefinedNode.getValue()), labelProvider.getColumnText(undefinedNode, 0));
    assertEquals(dateLabels.getText(dateNode.getValue()), labelProvider.getColumnText(dateNode, 0));
    assertEquals(workspaceLabels.getText(workspaceNode.getValue()), labelProvider.getColumnText(workspaceNode, 0));
  }

  @Test
  public void getColumnText_2() throws Exception {
    Command defined = (Command) definedNode.getValue();
    Command undefined = (Command) undefinedNode.getValue();

    ICommandData d1 = mock(ICommandData.class);
    ICommandData d2 = mock(ICommandData.class);
    given(d1.get(ICommandData.COMMAND)).willReturn(defined);
    given(d2.get(ICommandData.COMMAND)).willReturn(undefined);
    given(d1.get(ICommandData.DATE)).willReturn(new LocalDate());
    given(d2.get(ICommandData.DATE)).willReturn(new LocalDate());
    given(d1.get(ICommandData.COUNT)).willReturn(Integer.valueOf(10));
    given(d2.get(ICommandData.COUNT)).willReturn(Integer.valueOf(200));
    contents.getViewer().setInput(CommandPageContentProviderTest.newInput(d1, d2));

    contents.setSelectedCategories(Category.COMMAND);
    contents.setPaintCategory(Category.COMMAND);
    TreeNode root = contents.getRoot();
    TreeNode[] commandNodes = root.getChildren();
    assertEquals(2, commandNodes.length);
    assertEquals(d1.get(ICommandData.COUNT) + "", labelProvider.getColumnText(commandNodes[0], 2));
    assertEquals(d2.get(ICommandData.COUNT) + "", labelProvider.getColumnText(commandNodes[1], 2));

    contents.setSelectedCategories(Category.DATE);
    contents.setPaintCategory(Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode dateNode = root.getChildren()[0];
    assertEquals(d1.get(ICommandData.COUNT) + d2.get(ICommandData.COUNT) + "",
        labelProvider.getColumnText(dateNode, 2));
  }

  @Test
  public void getColumnText_1() throws Exception {
    Command defined = (Command) definedNode.getValue();
    Command undefined = (Command) undefinedNode.getValue();
    assertEquals(defined.getDescription(), labelProvider.getColumnText(defined, 1));
    assertNull(labelProvider.getColumnText(undefined, 1));
  }

  @Test
  public void getForeground() {
    assertNull(labelProvider.getForeground(definedNode));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), labelProvider.getForeground(undefinedNode));
    assertEquals(workspaceLabels.getForeground(workspaceNode.getValue()), labelProvider.getForeground(workspaceNode));
  }
}
