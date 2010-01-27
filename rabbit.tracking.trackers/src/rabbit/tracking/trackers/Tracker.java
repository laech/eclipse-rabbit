package rabbit.tracking.trackers;

import rabbit.tracking.core.ITracker;


/**
 * Defines common behaviors for a tracker.
 */
public abstract class Tracker implements ITracker {

	/** Variable to indicate whether this tracker is activated. */
	private boolean isEnabled;

	/**
	 * Constructs a new tracker.
	 */
	public Tracker() {
		isEnabled = false;
	}

	/**
	 * Enables this tracker with the necessary operations.
	 * <p>
	 * This method will be called by {@link #setEnabled(boolean)} if the
	 * conditions are satisfied. Subclasses should override this method to
	 * enable this tracker.
	 * </p>
	 * <p>
	 * Precondition: {@link #isEnabled()} returns false.<br />
	 * Postconditions: {@link #isEnabled()} returns <tt>true</tt> and this
	 * tracker is enabled.
	 * </p>
	 * 
	 * @see #setEnabled(boolean)
	 */
	protected abstract void doEnable();

	/**
	 * Disables this tracker with the necessary operations..
	 * <p>
	 * This method will be called by {@link #setEnabled(boolean)} if the
	 * conditions are satisfied. Subclasses should override this method to
	 * disable this tracker.
	 * </p>
	 * <p>
	 * Precondition: {@link #isEnabled()} returns true.<br />
	 * Postconditions: {@link #isEnabled()} returns <tt>false</tt> and this
	 * tracker is disabled.
	 * </p>
	 * 
	 * @see #setEnabled(boolean)
	 */
	protected abstract void doDisable();

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		if (isEnabled() != enable) {
			if (enable) {
				doEnable();
			} else {
				doDisable();
			}
			isEnabled = enable;
		}
	}
}
