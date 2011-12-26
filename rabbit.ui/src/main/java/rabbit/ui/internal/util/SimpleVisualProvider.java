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
package rabbit.ui.internal.util;

import com.google.common.base.Objects;

import java.util.Observable;

/**
 * A simple, observable {@link IVisualProvider}.
 */
public final class SimpleVisualProvider extends Observable
    implements IVisualProvider {

  private ICategory category;

  /**
   * Creates an object with no initial category.
   */
  public SimpleVisualProvider() {
    this(null);
  }

  /**
   * Creates an object with an initial category.
   * 
   * @param initialCategory the optional initial category of this object
   */
  public SimpleVisualProvider(ICategory initialCategory) {
    this.category = initialCategory;
  }

  @Override
  public ICategory getVisualCategory() {
    return category;
  }

  @Override
  public boolean setVisualCategory(ICategory category) {
    if (!Objects.equal(this.category, category)) {
      this.category = category;
      setChanged();
      notifyObservers();
    }
    return true;
  }

}
