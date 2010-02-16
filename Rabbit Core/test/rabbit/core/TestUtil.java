package rabbit.core;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.preference.IPreferenceStore;

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

	public static void setUpPathForTesting() {
		String path = RabbitCore.getDefault().getStoragePath().toOSString();
		path += File.separator;
		path += "TestFiles";
		IPreferenceStore pre = RabbitCore.getDefault().getPreferenceStore();
		pre.setValue(RabbitCore.STORAGE_LOCATION, path);
	}
}
