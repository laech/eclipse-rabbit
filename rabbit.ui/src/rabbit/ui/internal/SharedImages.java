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

public class SharedImages {

  public static final ImageDescriptor REFRESH = imageDescriptorFromPlugin(
      PLUGIN_ID, "icons/full/obj16/refresh.gif");

  public static final ImageDescriptor EXPAND_ALL = imageDescriptorFromPlugin(
      PLUGIN_ID, "icons/full/obj16/expandall.gif");

  public static final ImageDescriptor CALENDAR = imageDescriptorFromPlugin(
      PLUGIN_ID, "/icons/full/obj16/calendar.png");

  public static final ImageDescriptor TIME_HIERARCHY = imageDescriptorFromPlugin(
      PLUGIN_ID, "/icons/full/obj16/time-hierarchy.png");

  public static final ImageDescriptor GENERIC_ELEMENT = imageDescriptorFromPlugin(
      PLUGIN_ID, "/icons/full/obj16/generic-element.gif");
}
