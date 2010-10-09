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
package rabbit.ui.internal.pages;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

import java.util.Collection;

/**
 * @see CommandLabelProvider
 */
public class CommandLabelProviderTest {

  private final CommandLabelProvider labels;
  private final Command definedCommand;
  private final Command undefinedCommand;
  
  private final ICommandService commandService = (ICommandService) PlatformUI
      .getWorkbench().getService(ICommandService.class);
  
  private final ICommandImageService imageService = (ICommandImageService)
      PlatformUI.getWorkbench().getService(ICommandImageService.class);

  public CommandLabelProviderTest() {
    labels = new CommandLabelProvider();
    definedCommand = commandService.getDefinedCommands()[0];
    undefinedCommand = commandService.getCommand(System.currentTimeMillis() + "");
  }

  @Test
  public void testGetText() throws Exception {
    assertEquals(definedCommand.getName(), labels.getText(definedCommand));
    assertEquals(undefinedCommand.getId(), labels.getText(undefinedCommand));
  }

  @Test
  public void testGetImage_notNull() throws Exception {
    @SuppressWarnings("unchecked")
    Collection<String> ids = commandService.getDefinedCommandIds();
    for (String id : ids) {
      if (imageService.getImageDescriptor(id) != null) {
        Command cmdWithImage = commandService.getCommand(id);
        assertThat(labels.getImage(cmdWithImage), notNullValue());
        break;
      }
    }
  }
  
  @Test
  public void testGetImage_null() {
    assertNull(labels.getImage(undefinedCommand));
  }
}
