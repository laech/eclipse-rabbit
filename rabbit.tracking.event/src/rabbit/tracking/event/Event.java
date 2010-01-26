package rabbit.tracking.event;

import java.util.Calendar;

public class Event {

	private Calendar time;
	private long duration;
	
	public Event(Calendar time, long duration) {
		setTime(time);
		setDuration(duration);
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
