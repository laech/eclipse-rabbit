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
package rabbit.ui.internal.pages;

import static rabbit.ui.MillisConverter.toDefaultString;

import rabbit.ui.MillisConverter;
import rabbit.ui.CellPainter.IValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

/**
 * A {@link ColumnLabelProvider} that uses an {@link IValueProvider} to get
 * values for an element.
 * 
 * If {@link IValueProvider#shouldPaint(Object)} returns false on an element,
 * the element will not be labeled. If the method returns true, then
 * {@link IValueProvider#getValue(Object)} will be called to get the value for
 * the element, finally, {@link MillisConverter#toDefaultString(long)} is called
 * to format the returned value to become a label.
 */
public class ValueColumnLabelProvider extends ColumnLabelProvider {

  private final IValueProvider provider;

  /**
   * Constructor.
   * 
   * @param content The value provider.
   */
  public ValueColumnLabelProvider(IValueProvider provider) {

    checkNotNull(provider);
    this.provider = provider;
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    Object element = cell.getElement();
    if (provider.shouldPaint(element))
      cell.setText(toDefaultString(provider.getValue(element)));
    else
      cell.setText(null);
  }
}
