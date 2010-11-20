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
package rabbit.ui.internal.categories;

import rabbit.ui.internal.util.ICategory;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Contains categories for session data.
 */
public enum SessionCategory implements ICategory {

  /**
   * Represents the date category.
   */
  DATE(SharedCategory.DATE),

  /**
   * Represents the workspace category.
   */
  WORKSPACE(SharedCategory.WORKSPACE);

  private final ICategory category;

  SessionCategory(ICategory category) {
    this.category = category;
  }

  @Override
  public String getText() {
    return category.getText();
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return category.getImageDescriptor();
  }
}
