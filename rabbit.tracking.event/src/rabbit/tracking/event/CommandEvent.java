package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.core.commands.ExecutionEvent;

/**
 * k
 * @author o-o
 *
 */
public class CommandEvent extends DiscreteEvent {

	private ExecutionEvent event;
	
	public CommandEvent(Calendar time, ExecutionEvent e) {
		super(time);
		event = e;
	}

	public ExecutionEvent getExecutionEvent() {
		return event;
	}

	public void setExecutionEvent(ExecutionEvent event) {
		this.event = event;
	}
	
}
