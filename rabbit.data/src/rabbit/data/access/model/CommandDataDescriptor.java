package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
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
   * Finds the command from the workbench that has the command ID of this
   * object. If no command has the ID, then an undefined command is returned.
   * @return The command, either defined or undefined.
   */
  @Nonnull
  public final Command findCommand() {
    ICommandService service = (ICommandService) PlatformUI.getWorkbench()
        .getService(ICommandService.class);
    return service.getCommand(getCommandId());
  }

  /**
   * Gets the command ID of the command.
   * 
   * @return The command ID, never null.
   */
  @Nonnull
  public final String getCommandId() {
    return commandId;
  }
}
