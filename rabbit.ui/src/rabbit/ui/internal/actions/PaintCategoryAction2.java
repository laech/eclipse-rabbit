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
package rabbit.ui.internal.actions;

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.TreePathValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;

/**
 * An action to call {@link TreePathValueProvider#setVisualCategory(ICategory)} when it's
 * invoked.
 */
public final class PaintCategoryAction2 extends Action {

  private final TreePathValueProvider provider;
  private final ICategory visualCategory;

  /**
   * Constructor. When this action is invoked,
   * {@link TreePathValueProvider#setVisualCategory(ICategory)} will be called with the given
   * category. The text/image will be taken from the category.
   * @param provider the provider to set the category.
   * @param visualCategory the category to be set.
   * @throws NullPointerException if any argument is null.
   */
  public PaintCategoryAction2(TreePathValueProvider provider, ICategory visualCategory) {
    this.provider = checkNotNull(provider);
    this.visualCategory = checkNotNull(visualCategory);
    setText(visualCategory.getText());
    setImageDescriptor(visualCategory.getImageDescriptor());
  }

  @Override
  public void run() {
    super.run();
    provider.setVisualCategory(visualCategory);
  }
}
