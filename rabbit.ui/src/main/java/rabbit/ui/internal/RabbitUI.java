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

import rabbit.ui.internal.extension.CategoryDescriptor;
import rabbit.ui.internal.extension.PageDescriptor;

import com.google.common.collect.ImmutableList;

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.util.List;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitUI extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "rabbit.ui";

  private static final String UI_PAGE_EXTENSION_ID = "rabbit.ui.pages";

  public static final String DEFAULT_DISPLAY_DATE_PERIOD = "defaultDisplayDatePeriod";

  // The shared instance
  private static RabbitUI plugin;

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static RabbitUI getDefault() {
    return plugin;
  }

  /**
   * The constructor
   */
  public RabbitUI() {
  }

  /**
   * Gets the default number of days to display the data in the main view.
   * 
   * @return The default number of days.
   */
  public int getDefaultDisplayDatePeriod() {
    return getPreferenceStore().getInt(DEFAULT_DISPLAY_DATE_PERIOD);
  }

  /**
   * Sets the default number of days to display the data in the main view.
   * 
   * @param numDays The number of days.
   */
  public void setDefaultDisplayDatePeriod(int numDays) {
    IPreferenceStore store = getPreferenceStore();
    store.setValue(DEFAULT_DISPLAY_DATE_PERIOD, numDays);
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
   * Loads the page category extensions.
   * 
   * @return an unmodifiable list of categories, or an empty list if no
   *         extensions found
   */
  public List<CategoryDescriptor> getPageCategories() {
    final ImmutableList.Builder<CategoryDescriptor> builder =
        ImmutableList.builder();

    final IConfigurationElement[] elements = getExtensionRegistry()
        .getConfigurationElementsFor(UI_PAGE_EXTENSION_ID);

    for (IConfigurationElement element : elements) {
      if (CategoryDescriptor.isCategoryElement(element)) {
        try {
          builder.add(CategoryDescriptor.from(element));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        }
      }
    }
    return builder.build();
  }

  /**
   * Loads a list of page extensions.
   * 
   * @return an unmodifiable list of pages, or an empty list if no pages found
   */
  public List<PageDescriptor> getPages() {
    final ImmutableList.Builder<PageDescriptor> builder =
        ImmutableList.builder();

    final IConfigurationElement[] elements = getExtensionRegistry()
        .getConfigurationElementsFor(UI_PAGE_EXTENSION_ID);

    for (IConfigurationElement element : elements) {
      if (PageDescriptor.isPageElement(element)) {
        try {
          builder.add(PageDescriptor.from(element));
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        }
      }
    }
    return builder.build();
  }
}
