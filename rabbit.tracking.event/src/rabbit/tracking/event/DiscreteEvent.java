package rabbit.tracking.event;

import java.util.Calendar;

public class DiscreteEvent {

	private Calendar time;

	public DiscreteEvent(Calendar time) {
		setTime(time);
	}

	public Calendar getTime() {
		return time;
	}

	/**
	 * 
	 * @param time
	 * @throws IllegalArgumentException
	 */
	public void setTime(Calendar time) {
		if (time == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		this.time = time;
	}
}
