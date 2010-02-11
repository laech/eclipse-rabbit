package rabbit.core.internal;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import rabbit.core.internal.storage.xml.XmlResourceManager;

public class ResourceDataAttacher implements IStartup {

	@Override
	public void earlyStartup() {
		PlatformUI.getWorkbench().addWorkbenchListener(XmlResourceManager.INSTANCE);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(XmlResourceManager.INSTANCE);
	}

}
