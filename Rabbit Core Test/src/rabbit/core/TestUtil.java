package rabbit.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

public class TestUtil {

	private static final IPath TestPath = RabbitCore.getDefault().getStoragePath().append("TestFiles");

	static {
		IPreferenceStore pre = RabbitCore.getDefault().getPreferenceStore();
		pre.setValue(RabbitCore.STORAGE_LOCATION, TestPath.toOSString());
	}

	/**
	 * Creates a new tracker for testing. The trackers getData method will
	 * return a modifiable collection for testing purposes.
	 * 
	 * @return A new tracker.
	 */
	public static <T> ITracker<T> newTracker() {

		return new ITracker<T>() {

			private boolean isEnabled = false;
			private Set<T> data = new HashSet<T>();

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
				return data;
			}

			@Override
			public void flushData() {
				data.clear();
			}

			@Override
			public void saveData() {
			}

		};
	}

	public static void setUpPathForTesting() {
		// Done in static constructor.
	}

}
