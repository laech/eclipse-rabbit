package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.LocalDate;

/**
 * Data descriptor for command events.
 */
public class CommandDataDescriptor extends DateDescriptor {

  private static final ICommandService SERVICE = (ICommandService) PlatformUI
      .getWorkbench().getService(ICommandService.class);
  
  private final String commandId;
  private final int count;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param count The execution count of the command.
   * @param commandId The id of the command.
   * @throws NullPointerException If date is null, or commandId is null.
   * @throws IllegalArgumentException If count is < 0, or commandId has 0
   *           length.
   */
  public CommandDataDescriptor(LocalDate date, int count, String commandId) {
    super(date);

    checkArgument(count >= 0);
    this.count = count;

    checkNotNull(commandId);
    checkArgument(commandId.length() > 0);
    this.commandId = commandId;
  }

  /**
   * Finds the command from the workbench that has the command ID of this
   * object. If no command has the ID, then an undefined command is returned.
   * 
   * @return The command, either defined or undefined.
   */
  public final Command findCommand() {
    return SERVICE.getCommand(getCommandId());
  }

  /**
   * Gets the command ID of the command.
   * 
   * @return The command ID, never null.
   */
  public final String getCommandId() {
    return commandId;
  }

  /**
   * @return The execution count.
   */
  public int getCount() {
    return count;
  }
}
