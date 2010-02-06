package rabbit.core;

import java.util.Collection;
import java.util.Collections;

import rabbit.core.ITracker;

public class TestUtil {

	/**
	 * Creates a new tracker for testing.
	 * 
	 * @return A new tracker.
	 */
	public static <T> ITracker<T> newTracker() {

		return new ITracker<T>() {

			boolean isEnabled = false;

			@Override
			public boolean isEnabled() {
				return isEnabled;
			}

			@Override
			public void setEnabled(boolean enable) {
				isEnabled = enable;
			}

			@Override
			public Collection<T> getData() {
				return Collections.emptyList();
			}

			@Override
			public void flushData() {
			}

			@Override
			public void saveData() {
			}

		};
	}
}
