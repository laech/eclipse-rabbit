package rabbit.tracking.ui.pages.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PagePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "rabbit.tracking.ui.pages";

	// The shared instance
	private static PagePlugin plugin;

	/**
	 * The constructor
	 */
	public PagePlugin() {}

	@Override public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PagePlugin getDefault() {
		return plugin;
	}

}
