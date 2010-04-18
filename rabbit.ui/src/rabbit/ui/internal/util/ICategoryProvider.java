/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.util;

/**
 * Represents a provider containing categories.
 */
public interface ICategoryProvider {

  /**
   * Gets the categories that are not in use.
   * 
   * @return The categories that are not in use.
   */
  ICategory[] getUnselectedCategories();

  /**
   * Gets the categories that are in use.
   * 
   * @return The categories that are in use.
   */
  ICategory[] getSelectedCategories();

  /**
   * Sets the selected categories.
   * 
   * @param categories The new categories. If the new categories is null or
   *          empty, the default categories will be used.
   */
  void setSelectedCategories(ICategory... categories);
}
