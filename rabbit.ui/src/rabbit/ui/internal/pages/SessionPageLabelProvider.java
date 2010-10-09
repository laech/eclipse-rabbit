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

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.ui.internal.viewers.CellPainter.IValueProvider;
import rabbit.ui.internal.viewers.TreeNodes;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider for a {@link SessionPage}
 */
public class SessionPageLabelProvider extends LabelProvider
    implements ITableLabelProvider {

  private final DateLabelProvider dateLabels;
  private final IValueProvider valueProvider;

  /**
   * Constructor.
   * @param valueProvider The value provider.
   */
  public SessionPageLabelProvider(IValueProvider valueProvider) {
    this.valueProvider = checkNotNull(valueProvider);
    this.dateLabels = new DateLabelProvider();
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
  }
  
  @Override
  public Image getImage(Object element) {
    element = TreeNodes.getObject(element);
    return dateLabels.getImage(element);
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return (columnIndex == 0) ? getImage(element) : null;
  }
  
  @Override
  public String getText(Object element) {
    element = TreeNodes.getObject(element);
    return dateLabels.getText(element);
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    Object value = TreeNodes.getObject(element);
    switch (columnIndex) {
    case 0:
      return getText(value);

    case 1:
      return format(valueProvider.getValue(element));
        
    default:
      return null;
    }
  }
}
