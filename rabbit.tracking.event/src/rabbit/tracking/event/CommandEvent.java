package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.core.commands.ExecutionEvent;

public class CommandEvent extends Event {

	private ExecutionEvent event;
	
	public CommandEvent(Calendar time, ExecutionEvent e) {
		super(time, 0);
		event = e;
	}

	public ExecutionEvent getExecutionEvent() {
		return event;
	}

	public void setExecutionEvent(ExecutionEvent event) {
		this.event = event;
	}
	
}
