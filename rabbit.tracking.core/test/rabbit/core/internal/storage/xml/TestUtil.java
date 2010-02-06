package rabbit.core.internal.storage.xml;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;

import rabbit.core.RabbitCore;

public class TestUtil {

	public static void setUpPathForTesting() {
		String path = RabbitCore.getDefault().getStoragePath().toOSString();
		path += File.separator;
		path += "TestFiles";
		IPreferenceStore pre = RabbitCore.getDefault().getPreferenceStore();
		pre.setValue(RabbitCore.STORAGE_LOCATION, path);
	}
}
