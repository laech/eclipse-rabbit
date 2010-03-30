package rabbit.ui.internal;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;
import static rabbit.ui.internal.RabbitUI.PLUGIN_ID;

import org.eclipse.jface.resource.ImageDescriptor;

// TODO test
public class SharedImages {

	public static final ImageDescriptor REFRESH =
			imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/refresh.gif");

	public static final ImageDescriptor EXPAND_ALL =
			imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/expandall.gif");

	public static final ImageDescriptor RUN =
			imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/run.gif");

	public static final ImageDescriptor DEBUG =
			imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/debug.gif");

	public static final ImageDescriptor PROFILE =
			imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/profile.gif");
}
