package rabbit.tracking.core;

/**
 * Represents a tracker that tracks events.
 */
public interface ITracker {

	/**
	 * Checks whether this tracker is enabled.
	 * 
	 * @return <tt>true</tt> if this tracker is enabled, <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isEnabled();

	/**
	 * Enables or disables this tracker. When disabled, this tracker will not
	 * track any events.
	 * 
	 * @param enable
	 *            <tt>true</tt> to enable this tracker, <tt>false</tt> to disable
	 *            this tracker. Calling this method will have no effect if
	 *            <tt>(enable == {@link #isEnabled()})</tt>.
	 */
	public void setEnabled(boolean enable);

}
