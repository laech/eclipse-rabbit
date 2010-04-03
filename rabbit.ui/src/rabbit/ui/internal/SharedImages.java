package rabbit.ui.internal;

import static rabbit.ui.internal.RabbitUI.PLUGIN_ID;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import org.eclipse.jface.resource.ImageDescriptor;

public class SharedImages {

  public static final ImageDescriptor REFRESH = imageDescriptorFromPlugin(
      PLUGIN_ID, "icons/full/obj16/refresh.gif");

  public static final ImageDescriptor EXPAND_ALL = imageDescriptorFromPlugin(
      PLUGIN_ID, "icons/full/obj16/expandall.gif");
}
