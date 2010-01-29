package rabbit.tracking.ui;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.ui.pages.PageExtension;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "rabbit.tracking.ui";

	public static final String UI_PAGE_EXTENSION_ID = "rabbit.tracking.ui.pages";

	// The shared instance
	private static Activator plugin;

	private SortedSet<PageExtension> pages;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		pages = new TreeSet<PageExtension>();
		for (IConfigurationElement e : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(UI_PAGE_EXTENSION_ID)) {
			PageExtension p = recursiveGet(e);
			if (p != null) {
				pages.add(p);
			}
		}
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
	
	public Set<PageExtension> getPages() {
		return pages;
	}

	private PageExtension recursiveGet(IConfigurationElement e) {
		
		String name = e.getAttribute("name");
		String desc = e.getAttribute("description");
		
		Object o = null;
		try {
			o = e.createExecutableExtension("class");
			
		} catch (CoreException ex) {
			ex.printStackTrace();
			return null;
		}
		
		if (!(o instanceof IPage)) {
			return null;
		}

		IPage page = (IPage) o;
		PageExtension extension = new PageExtension(name, page, page.getIcon(), desc);
		
		for (IConfigurationElement child : e.getChildren()) {
			PageExtension p = recursiveGet(child);
			if (p != null) {
				extension.addChild(p);
			}
		}
		return extension;
	}
}
