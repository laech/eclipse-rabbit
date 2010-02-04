package rabbit.tracking.core;

import java.util.Collection;

/**
 * Represents a tracker that tracks events.
 */
public interface ITracker<T> {

	/**
	 * Checks whether this tracker is enabled.
	 * 
	 * @return <tt>true</tt> if this tracker is enabled, <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isEnabled();

	/**
	 * Enables or disables this tracker. When disabled, this tracker will not
	 * track any events, and previous data will be saved. When enabled, all 
	 * previous data will be flushed.
	 * 
	 * @param enable
	 *            <tt>true</tt> to enable this tracker, <tt>false</tt> to disable
	 *            this tracker. Calling this method will have no effect if
	 *            <tt>(enable == {@link #isEnabled()})</tt>.
	 */
	public void setEnabled(boolean enable);
	
	/**
	 * Gets the data collected by this tracker.
	 * @return The data.
	 */
	public Collection<T> getData();
	
	/**
	 * Flushes the data collected by this tracker.
	 */
	public void flushData();
	
	/**
	 * Saves the data collected by this tracker.
	 */
	public void saveData();

}
