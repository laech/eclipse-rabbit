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

import rabbit.data.access.model.SessionDataDescriptor;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider for a {@link SessionPage}
 */
public class SessionPageLabelProvider extends LabelProvider implements
    ITableLabelProvider {

  private final DateLabelProvider dateLabels;

  /**
   * Constructor.
   */
  public SessionPageLabelProvider() {
    dateLabels = new DateLabelProvider();
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
  }
  
  @Override
  public Image getImage(Object element) {
    if (element instanceof SessionDataDescriptor)
      return dateLabels.getImage(((SessionDataDescriptor) element).getDate());
    else
      return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return (columnIndex == 0) ? getImage(element) : null;
  }
  
  @Override
  public String getText(Object element) {
    if (element instanceof SessionDataDescriptor)
      return dateLabels.getText(((SessionDataDescriptor) element).getDate());
    else
      return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      return getText(element);

    case 1:
      if (element instanceof SessionDataDescriptor)
        return format(((SessionDataDescriptor) element).getValue());
      else
        return null;

    default:
      return null;
    }
  }
}
