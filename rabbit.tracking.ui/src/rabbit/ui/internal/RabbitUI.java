package rabbit.ui.internal;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.ui.pages.IPage;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitUI extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "rabbit.ui";

	public static final String UI_PAGE_EXTENSION_ID = "rabbit.ui.pages";

	// The shared instance
	private static RabbitUI plugin;

	private SortedSet<PageDescriptor> pages;

	/**
	 * The constructor
	 */
	public RabbitUI() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		pages = new TreeSet<PageDescriptor>(new Comparator<PageDescriptor>() {
			@Override
			public int compare(PageDescriptor o1, PageDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (IConfigurationElement e : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(UI_PAGE_EXTENSION_ID)) {
			PageDescriptor p = recursiveGet(e);
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
	public static RabbitUI getDefault() {
		return plugin;
	}

	/**
	 * Gets the pages.
	 * 
	 * @return The pages.
	 */
	Set<PageDescriptor> getPages() {
		return pages;
	}

	/**
	 * Recursively builds a tree out of the given element.
	 * 
	 * @param e
	 *            The element.
	 * @return A tree.
	 */
	private PageDescriptor recursiveGet(IConfigurationElement e) {

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
		PageDescriptor extension = new PageDescriptor(name, page, desc);

		for (IConfigurationElement child : e.getChildren()) {
			PageDescriptor p = recursiveGet(child);
			if (p != null) {
				extension.addChild(p);
			}
		}
		return extension;
	}
}
