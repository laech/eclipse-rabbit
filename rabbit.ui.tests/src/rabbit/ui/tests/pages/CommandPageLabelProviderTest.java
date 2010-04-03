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

import rabbit.ui.internal.pages.CommandPage;
import rabbit.ui.internal.pages.CommandPageLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Test for {@link CommandPageLabelProvider}
 */
public class CommandPageLabelProviderTest {

  private static Shell shell;
  private static CommandPage page;
  private static CommandPageLabelProvider provider;

  private static Command definedCommand;
  private static Command undefinedCommand;

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new CommandPage();
    page.createContents(shell);
    provider = new CommandPageLabelProvider(page);
    definedCommand = getCommandService().getDefinedCommands()[0];
    undefinedCommand = getCommandService().getCommand(
        System.currentTimeMillis() + "");
  }

  private static ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }

  @Test
  public void testGetBackground() {
    assertNull(provider.getBackground(definedCommand));
    assertNull(provider.getBackground(undefinedCommand));
  }

  @Test
  public void testGetColumnImage() {
    assertNull(provider.getColumnImage(definedCommand, 0));
    assertNull(provider.getColumnImage(definedCommand, 1));
    assertNull(provider.getColumnImage(definedCommand, 2));
    assertNull(provider.getColumnImage(undefinedCommand, 0));
    assertNull(provider.getColumnImage(undefinedCommand, 1));
    assertNull(provider.getColumnImage(undefinedCommand, 2));
  }

  @Test
  public void testGetColumnText() throws Exception {
    Map<Command, Long> data = getDataMap(page);

    long definedValue = 1000;
    data.put(definedCommand, definedValue);

    long undefinedValue = 19489;
    data.put(undefinedCommand, undefinedValue);

    page.getViewer().setInput(data.keySet());

    assertEquals(definedCommand.getName(), provider.getColumnText(
        definedCommand, 0));
    assertEquals(definedCommand.getDescription(), provider.getColumnText(
        definedCommand, 1));
    assertEquals(String.valueOf(definedValue), provider.getColumnText(
        definedCommand, 2));

    assertEquals(undefinedCommand.getId(), provider.getColumnText(
        undefinedCommand, 0));
    assertNull(provider.getColumnText(undefinedCommand, 1));
    assertEquals(String.valueOf(undefinedValue), provider.getColumnText(
        undefinedCommand, 2));
  }

  @Test
  public void testGetForeground() {
    assertNull(provider.getForeground(definedCommand));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(undefinedCommand));
  }

  @SuppressWarnings("unchecked")
  private Map<Command, Long> getDataMap(CommandPage page) throws Exception {
    Field field = CommandPage.class.getDeclaredField("dataMapping");
    field.setAccessible(true);
    return (Map<Command, Long>) field.get(page);
  }
}
