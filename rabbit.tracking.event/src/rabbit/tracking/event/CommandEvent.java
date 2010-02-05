package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * A command execution event.
 */
public class CommandEvent extends DiscreteEvent {

	private ExecutionEvent event;

	/**
	 * Constructs a new event.
	 * 
	 * @param time The time of the event.
	 * @param e The execution event.
	 */
	public CommandEvent(Calendar time, ExecutionEvent e) {
		super(time);
		event = e;
	}

	/**
	 * Gets the execution event.
	 * 
	 * @return The execution event.
	 */
	public ExecutionEvent getExecutionEvent() {
		return event;
	}

	/**
	 * Sets the execution event.
	 * 
	 * @param event The execution event.
	 */
	public void setExecutionEvent(ExecutionEvent event) {
		this.event = event;
	}

}
