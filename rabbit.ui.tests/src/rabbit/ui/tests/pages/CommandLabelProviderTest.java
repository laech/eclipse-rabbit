/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.CommandLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

/**
 * @see CommandLabelProvider
 */
@SuppressWarnings("restriction")
public class CommandLabelProviderTest {

  private final CommandLabelProvider labels;
  private final Command definedCommand;
  private final Command undefinedCommand;

  public CommandLabelProviderTest() {
    labels = new CommandLabelProvider();

    ICommandService service = (ICommandService) PlatformUI.getWorkbench()
        .getService(ICommandService.class);
    definedCommand = service.getDefinedCommands()[0];
    undefinedCommand = service.getCommand(System.currentTimeMillis() + "");
  }

  @Test
  public void testGetText() throws Exception {
    assertEquals(definedCommand.getName(), labels.getText(definedCommand));
    assertEquals(undefinedCommand.getId(), labels.getText(undefinedCommand));
  }

  @Test
  public void testGetImage() throws Exception {
    /*
     * Image will never be null, if there is no image associated with a command,
     * a generic image should be returned.
     */

    assertNotNull(labels.getImage(definedCommand));
    assertNotNull(labels.getImage(undefinedCommand));
  }
}
