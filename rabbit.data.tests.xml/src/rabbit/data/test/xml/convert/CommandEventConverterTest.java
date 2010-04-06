package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.CommandEventConverter;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.store.model.CommandEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.DateTime;

import java.util.Collections;

/**
 * @see CommandEventConverter
 */
@SuppressWarnings("restriction")
public class CommandEventConverterTest extends
    AbstractConverterTest<CommandEvent, CommandEventType> {

  @Override
  protected CommandEventConverter createConverter() {
    return new CommandEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    CommandEvent event = createEvent();
    CommandEventType type = converter.convert(event);
    assertEquals(event.getExecutionEvent().getCommand().getId(), type
        .getCommandId());
    assertEquals(1, type.getCount());
  }

  private CommandEvent createEvent() {
    return new CommandEvent(new DateTime(), createExecutionEvent("adnk2o385"));
  }

  private ExecutionEvent createExecutionEvent(String commandId) {
    return new ExecutionEvent(getCommandService().getCommand(commandId),
        Collections.EMPTY_MAP, null, null);
  }

  private ICommandService getCommandService() {
    return (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }
}
