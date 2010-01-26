package rabbit.tracking.trackers;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.event.CommandEvent;
import rabbit.tracking.storage.xml.CommandEventStorer;
import rabbit.tracking.storage.xml.IXmlStorer;

/**
 * Tracks command executions.
 */
public class ExecutionTracker extends Tracker implements IExecutionListener {
	
	private Set<CommandEvent> events;

	/** Constructor. */
	public ExecutionTracker() {
		events = new LinkedHashSet<CommandEvent>();
	}

	@Override
	protected void doEnable() {
		getCommandService().addExecutionListener(this);
	}

	@Override
	protected void doDisable() {
		getCommandService().removeExecutionListener(this);
		IXmlStorer<CommandEvent> s = new CommandEventStorer<CommandEvent>();
		s.insert(events);
		s.write();
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
		events.add(new CommandEvent(Calendar.getInstance(), event));
	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		// Do nothing.
	}

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// Do nothing.
	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException e) {
		// Do nothing.
	}

}
