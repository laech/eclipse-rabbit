package rabbit.core.internal.trackers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import rabbit.core.ITracker;
import rabbit.core.storage.IStorer;

/**
 * Defines common behaviors for a tracker.
 */
public abstract class AbstractTracker<T> implements ITracker<T> {

	/** Variable to indicate whether this tracker is activated. */
	private boolean isEnabled;

	private Set<T> data;

	private IStorer<T> storer;

	/**
	 * Constructs a new tracker.
	 */
	public AbstractTracker() {
		isEnabled = false;
		data = new LinkedHashSet<T>();
		storer = createDataStorer();
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
				flushData();
			} else {
				doDisable();
				saveData();
			}
			isEnabled = enable;
		}
	}

	@Override
	public Collection<T> getData() {
		return Collections.unmodifiableSet(data);
	}

	@Override
	public void flushData() {
		data.clear();
	}

	@Override
	public void saveData() {
		if (!getData().isEmpty()) {
			storer.insert(getData());
			storer.commit();
		}
	}

	/**
	 * Adds an event data to the collection.
	 * 
	 * @param o
	 *            The data.
	 */
	protected void addData(T o) {
		data.add(o);
	}

	/**
	 * Creates a storer for storing the data.
	 * 
	 * @return A data storer.
	 */
	protected abstract IStorer<T> createDataStorer();
}
