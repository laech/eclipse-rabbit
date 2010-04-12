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
