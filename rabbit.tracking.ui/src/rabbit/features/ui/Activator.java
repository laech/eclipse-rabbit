package rabbit.features.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "rabbit.tracking.ui";

	public static final String UI_PAGE_EXTENSION_ID = "rabbit.tracking.ui.pages";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private void k() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(UI_PAGE_EXTENSION_ID);
		
		for (IConfigurationElement e : elements) {
			
			if (isCategory(e)) {
				
			}
		}
	}

	private boolean isCategory(IConfigurationElement e) {
		// TODO Auto-generated method stub
		return false;
	}
}
