package rabbit.ui.internal;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;
import static rabbit.ui.internal.RabbitUI.PLUGIN_ID;

import org.eclipse.jface.resource.ImageDescriptor;

public class SharedImages {

	public static final ImageDescriptor EXPAND_ALL =
			imageDescriptorFromPlugin(PLUGIN_ID, "resources/expandall.gif");
}
