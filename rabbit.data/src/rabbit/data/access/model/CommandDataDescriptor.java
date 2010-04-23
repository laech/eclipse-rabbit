package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * Data descriptor for command events.
 */
public class CommandDataDescriptor extends ValueDescriptor {

  @Nonnull
  private final String commandId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @throws NullPointerException If date is null, or commandId is null.
   * @throws IllegalArgumentException If count is < 0, or commandId has 0 length.
   */
  public CommandDataDescriptor(@Nonnull LocalDate date, @Nonnull long count,
      @Nonnull String commandId) {
    super(date, count);
    checkNotNull(commandId, "Command ID cannot be null");
    checkArgument(commandId.length() > 0);
    this.commandId = commandId;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getCommandId());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    CommandDataDescriptor des = (CommandDataDescriptor) obj;
    return des.getDate().equals(getDate()) && des.getValue() == getValue()
        && des.getCommandId().equals(getCommandId());
  }

  /**
   * Gets the command ID of the command.
   * 
   * @return The command ID, never null.
   */
  @Nonnull
  public String getCommandId() {
    return commandId;
  }

  /**
   * @return The command execution count.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }
}
