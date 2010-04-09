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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.ui.internal.pages.CommandPage;
import rabbit.ui.internal.pages.CommandPageContentProvider;
import rabbit.ui.internal.pages.CommandPageLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.commands.Command;
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
  private static CommandPage page;
  private static CommandPageLabelProvider labels;
  private static CommandPageContentProvider contents;

  private static Command defined;
  private static Command undefined;

  @AfterClass
  public static void afterClass() {
    labels.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new CommandPage();
    page.createContents(shell);
    contents = new CommandPageContentProvider(page, true);
    labels = new CommandPageLabelProvider(contents);
    defined = getCommandService().getDefinedCommands()[0];
    undefined = getCommandService().getCommand(System.currentTimeMillis() + "");

    page.getViewer().setContentProvider(contents);
    page.getViewer().setLabelProvider(labels);
  }

  private static ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }

  @Test
  public void testGetBackground() {
    assertNull(labels.getBackground(defined));
    assertNull(labels.getBackground(undefined));
    assertNull(labels.getBackground(new LocalDate()));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(labels.getColumnImage(defined, 0));
    assertNull(labels.getColumnImage(defined, 1));
    assertNull(labels.getColumnImage(defined, 2));

    assertNotNull(labels.getColumnImage(undefined, 0));
    assertNull(labels.getColumnImage(undefined, 1));
    assertNull(labels.getColumnImage(undefined, 2));

    assertNotNull(labels.getColumnImage(new LocalDate(), 0));
    assertNull(labels.getColumnImage(new LocalDate(), 1));
    assertNull(labels.getColumnImage(new LocalDate(), 2));
  }

  @Test
  public void testGetColumnText_0() throws Exception {
    assertEquals(defined.getName(), labels.getColumnText(defined, 0));
    assertEquals(undefined.getId(), labels.getColumnText(undefined, 0));

    LocalDate date = new LocalDate();
    CommandDataDescriptor des = new CommandDataDescriptor(date, 1, defined
        .getId());
    assertEquals(defined.getName(), labels.getColumnText(des, 0));

    des = new CommandDataDescriptor(date, 1, undefined.getId());
    assertEquals(undefined.getId(), labels.getColumnText(des, 0));
  }

  @Test
  public void testGetColumnText_2() throws Exception {
    CommandDataDescriptor d1;
    CommandDataDescriptor d2;
    LocalDate date = new LocalDate();
    d1 = new CommandDataDescriptor(date, 11, defined.getId());
    d2 = new CommandDataDescriptor(date, 102, d1.getCommandId());
    page.getViewer().setInput(Arrays.asList(d1, d2));

    assertEquals(d1.getValue() + "", labels.getColumnText(d1, 2));
    assertEquals(d2.getValue() + "", labels.getColumnText(d2, 2));
    assertEquals(d1.getValue() + d2.getValue() + "", labels.getColumnText(
        defined, 2));

    d1 = new CommandDataDescriptor(date, 100, undefined.getId());
    d2 = new CommandDataDescriptor(date, 9812, undefined.getId());
    page.getViewer().setInput(Arrays.asList(d1, d2));

    assertEquals(d1.getValue() + "", labels.getColumnText(d1, 2));
    assertEquals(d2.getValue() + "", labels.getColumnText(d2, 2));
    assertEquals(d1.getValue() + d2.getValue() + "", labels.getColumnText(
        undefined, 2));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    assertEquals(defined.getDescription(), labels.getColumnText(defined, 1));
    assertNull(labels.getColumnText(undefined, 1));

    LocalDate date = new LocalDate();
    CommandDataDescriptor des;
    des = new CommandDataDescriptor(date, 1, defined.getId());
    assertEquals(defined.getDescription(), labels.getColumnText(des, 1));

    des = new CommandDataDescriptor(date, 1, undefined.getId());
    assertNull(labels.getColumnText(des, 1));
  }

  @Test
  public void testGetForeground() {
    assertNull(labels.getForeground(defined));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        labels.getForeground(undefined));
  }
}
