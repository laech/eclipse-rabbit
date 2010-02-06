package rabbit.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.core.events.CommandEvent;
import rabbit.core.internal.storage.xml.CommandEventStorer;

/**
 * Tracks command executions.
 */
public class CommandTracker extends AbstractTracker<CommandEvent> implements IExecutionListener {

	/** Constructor. */
	public CommandTracker() {
		super();
	}

	@Override
	protected void doEnable() {
		getCommandService().addExecutionListener(this);
	}

	@Override
	protected void doDisable() {
		getCommandService().removeExecutionListener(this);
	}

	@Override
	protected CommandEventStorer createDataStorer() {
		return new CommandEventStorer();
	}

	/**
	 * Gets the workbench command service.
	 * 
	 * @return The command service.
	 */
	private ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(
				ICommandService.class);
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		addData(new CommandEvent(Calendar.getInstance(), event));
	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
	}

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException e) {
	}

}
