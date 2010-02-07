package rabbit.core.events;

import java.util.Calendar;

/**
 * An event that has a duration.
 */
public class ContinuousEvent extends DiscreteEvent {

	private long duration;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration in milliseconds.
	 * @throws IllegalArgumentException
	 *             If duration is negative.
	 */
	public ContinuousEvent(Calendar time, long duration) {
		super(time);
		setDuration(duration);
	}

	/**
	 * Gets the duration.
	 * 
	 * @return The duration in milliseconds.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the duration.
	 * 
	 * @param duration
	 *            The duration in milliseconds.
	 * @throws IllegalArgumentException
	 *             If duration is negative.
	 */
	public void setDuration(long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration cannot be negative.");
		}

		this.duration = duration;
	}
}
