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
package rabbit.ui.internal.extension;

import rabbit.ui.IPage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Represents a page extension descriptor.
 */
public final class PageDescriptor extends NamedExtensionDescriptor {

  /**
   * The name of a page element.
   */
  static final String ELEMENT_NAME = "page";

  /**
   * The ID attribute of a page element.
   */
  static final String ATTR_ID = "id";

  /**
   * The name attribute of a page element.
   */
  static final String ATTR_NAME = "name";

  /**
   * The category ID attribute of a page element.
   */
  static final String ATTR_CATEGORY_ID = "category";

  /**
   * The icon path attribute of a page element.
   */
  static final String ATTR_ICON_PATH = "icon";

  /**
   * The class attribute of a page element.
   */
  static final String ATTR_CLASS = "class";

  /**
   * Creates a {@link PageDescriptor} from an {@link IConfigurationElement}.
   * 
   * @param element the element to create from
   * @return a {@link PageDescriptor}, not null
   * @throws NullPointerException if {@code element} is {@code null}
   * @throws IllegalArgumentException if the element is not a page element or it
   *         does not contain enough attributes or it contains invalid
   *         attributes.
   */
  public static PageDescriptor from(IConfigurationElement element) {
    checkArgument(isPageElement(element),
        "element name is unexpected" + element.getName());

    final String id = element.getAttribute(ATTR_ID);
    final String name = element.getAttribute(ATTR_NAME);
    final String categoryId = element.getAttribute(ATTR_CATEGORY_ID);
    final String iconPath = element.getAttribute(ATTR_ICON_PATH);
    final String pluginId = element.getContributor().getName();
    final ImageDescriptor icon = (iconPath == null || pluginId == null) ? null
        : imageDescriptorFromPlugin(pluginId, iconPath);
    try {
      final Object page = element.createExecutableExtension(ATTR_CLASS);
      if (!(page instanceof IPage)) {
        throw new IllegalArgumentException("object (" + page
            + ") is not instance of " + IPage.class.getSimpleName());
      }
      return new PageDescriptor(id, name, (IPage)page, icon, categoryId);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException(e);
    } catch (CoreException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Checks whether the given element is a page element.
   * 
   * @param element the element to check
   * @return {@code true} is the element is a page element, {@code false}
   *         otherwise
   */
  public static boolean isPageElement(IConfigurationElement element) {
    checkNotNull(element, "element cannot be null");
    return ELEMENT_NAME.equals(element.getName());
  }

  private final IPage page;
  private final ImageDescriptor image;
  private final String categoryId;

  /**
   * Constructs a new page.
   * 
   * @param id the unique identifier of this page
   * @param name the name of this page
   * @param page the implementation of this page
   * @param icon the icon of this page
   * @param categoryId the ID of the category this extension belongs to
   * @throws NullPointerException if id, name, page is null
   */
  public PageDescriptor(String id, String name, IPage page,
      ImageDescriptor icon, String categoryId) {
    super(id, name);
    this.page = checkNotNull(page, "page cannot be null");
    this.image = icon;
    this.categoryId = categoryId;
  }

  /**
   * Gets the implementation of this page.
   * 
   * @return The implementation of this page, not null
   */
  public IPage getPage() {
    return page;
  }

  /**
   * Gets the icon of this page.
   * 
   * @return The icon of this page, may be null
   */
  public ImageDescriptor getIcon() {
    return image;
  }

  /**
   * Gets the ID of the category this page belongs to.
   * 
   * @return the ID of this page's category, may be null
   */
  public String getCategoryId() {
    return categoryId;
  }
}
