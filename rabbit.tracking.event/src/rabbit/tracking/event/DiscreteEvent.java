package rabbit.tracking.event;

import java.util.Calendar;

/**
 * Represents an event with no duration.
 */
public class DiscreteEvent {

	private Calendar time;

	/**
	 * Constructs a new event.
	 * 
	 * @param time The event time.
	 */
	public DiscreteEvent(Calendar time) {
		setTime(time);
	}

	/**
	 * Gets the time of the event.
	 * 
	 * @return The event time.
	 */
	public Calendar getTime() {
		return time;
	}

	/**
	 * Sets the time of the event.
	 * 
	 * @param time The time of the event.
	 * @throws IllegalArgumentException If argument is null.
	 */
	public void setTime(Calendar time) {
		if (time == null)
			throw new IllegalArgumentException("Argument cannot be null.");

		this.time = time;
	}
}
