package rabbit.tracking.event;

import java.util.Calendar;

public class ContinuousEvent extends DiscreteEvent {

	private long duration;

	public ContinuousEvent(Calendar time, long duration) {
		super(time);
		setDuration(duration);
	}

	public long getDuration() {
		return duration;
	}

	/**
	 * 
	 * @param duration
	 * @throws IllegalArgumentException
	 */
	public void setDuration(long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration cannot be negative.");
		}
		this.duration = duration;
	}
}
