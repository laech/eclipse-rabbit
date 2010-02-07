package rabbit.core.internal;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import rabbit.core.internal.storage.xml.ResourceData;

public class ResourceDataAttacher implements IStartup {

	@Override
	public void earlyStartup() {
		PlatformUI.getWorkbench().addWorkbenchListener(ResourceData.INSTANCE);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(ResourceData.INSTANCE);
	}

}
