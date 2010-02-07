package rabbit.core.events;

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
	 * @param time
	 *            The time of the event.
	 * @param e
	 *            The execution event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public CommandEvent(Calendar time, ExecutionEvent e) {
		super(time);
		setExecutionEvent(e);
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
	 * @param event
	 *            The execution event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setExecutionEvent(ExecutionEvent event) {
		if (event == null) {
			throw new NullPointerException("Argument cannot be null");
		}
		this.event = event;
	}

}
