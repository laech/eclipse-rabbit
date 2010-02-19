package rabbit.core.events;

import java.util.Calendar;

/**
 * Represents an event with no duration.
 */
public class DiscreteEvent {

	private Calendar time;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The event time.
	 * @throws NullPointerException
	 *             If argument is null.
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
	 * @param time
	 *            The time of the event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setTime(Calendar time) {
		if (time == null) {
			throw new NullPointerException();
		}

		this.time = time;
	}
}
