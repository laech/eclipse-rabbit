/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.internal;

import static rabbit.ui.internal.RabbitUI.PLUGIN_ID;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Shared image descriptors.
 */
public class SharedImages {

  public static final ImageDescriptor REFRESH = getObj16("refresh.gif");

  public static final ImageDescriptor EXPAND_ALL = getObj16("expandall.gif");

  public static final ImageDescriptor CALENDAR = getObj16("calendar.png");

  public static final ImageDescriptor TIME_HIERARCHY = getObj16("time-hierarchy.png");

  public static final ImageDescriptor ELEMENT = getObj16("element.gif");

  public static final ImageDescriptor HIERARCHY = getObj16("hierarchical.gif");

  public static final ImageDescriptor BRUSH = getObj16("line-color.gif");

  public static final ImageDescriptor SEARCH = getObj16("search.gif");
  
  public static final ImageDescriptor LAUNCH_TYPE = getObj16("launch-type.gif");
  
  public static final ImageDescriptor LAUNCH_MODE = getObj16("launch-mode.gif");
  
  public static final ImageDescriptor LAUNCH = getObj16("run.gif");
  
  /**
   * Gets an image descriptor from the directory "icons/full/obj16/".
   * @param name The name of the file.
   * @return An image descriptor, or null if not found.
   */
  private static ImageDescriptor getObj16(String name) {
    return imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/" + name);
  }
}
