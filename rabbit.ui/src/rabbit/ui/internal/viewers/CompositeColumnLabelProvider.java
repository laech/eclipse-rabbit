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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider that simply delegates method calls to other {@link ILabelProvider}s until a not
 * null value is found.
 */
public class CompositeColumnLabelProvider extends ColumnLabelProvider {

  private final ILabelProvider[] labelProviders;

  /**
   * Constructor.
   * @param labelProviders the actual providers to ask for labels.
   * @throws NullPointerException if {@code labelProviders} contains <code>null</code>.
   */
  public CompositeColumnLabelProvider(ILabelProvider... labelProviders) {
    for (ILabelProvider p : labelProviders) {
      checkNotNull(p);
    }
    this.labelProviders = labelProviders.clone();
  }

  @Override
  public String getText(Object element) {
    for (ILabelProvider provider : labelProviders) {
      String text = provider.getText(element);
      if (text != null) {
        return text;
      }
    }
    return super.getText(element);
  }

  @Override
  public Image getImage(Object element) {
    for (ILabelProvider provider : labelProviders) {
      Image image = provider.getImage(element);
      if (image != null) {
        return image;
      }
    }
    return super.getImage(element);
  }
}
