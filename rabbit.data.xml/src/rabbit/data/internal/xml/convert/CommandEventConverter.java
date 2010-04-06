package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.store.model.CommandEvent;

/**
 * Converts {@link CommandEvent} to {@link CommandEventType}.
 */
public class CommandEventConverter extends
    AbstractConverter<CommandEvent, CommandEventType> {

  @Override
  protected CommandEventType doConvert(CommandEvent element) {
    CommandEventType type = new CommandEventType();
    type.setCommandId(element.getExecutionEvent().getCommand().getId());
    type.setCount(1);
    return type;
  }

}
