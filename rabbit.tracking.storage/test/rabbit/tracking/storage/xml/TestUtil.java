package rabbit.tracking.storage.xml;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;

public class TestUtil {

	public static void setUpPathForTesting() {
		String path = StoragePlugin.getDefault().getStoragePath().toOSString();
		path += File.separator;
		path += "TestFiles";
		IPreferenceStore pre = StoragePlugin.getDefault().getPreferenceStore();
		pre.setValue(StoragePlugin.STORAGE_LOCATION, path);
	}
}
