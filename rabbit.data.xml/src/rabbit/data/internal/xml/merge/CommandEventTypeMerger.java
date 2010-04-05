package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.CommandEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link CommandEventType}.
 */
public class CommandEventTypeMerger extends AbstractMerger<CommandEventType> {

  @Override
  protected CommandEventType doMerge(CommandEventType t1, CommandEventType t2) {
    t1.setCount(t1.getCount() + t2.getCount());
    return t1;
  }

  @Override
  public boolean doIsMergeable(CommandEventType t1, CommandEventType t2) {
    return Objects.equal(t1.getCommandId(), t2.getCommandId());
  }

}
