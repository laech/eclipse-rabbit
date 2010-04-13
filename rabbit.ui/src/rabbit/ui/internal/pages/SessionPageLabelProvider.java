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

import static rabbit.ui.internal.util.MillisConverter.toDefaultString;

import rabbit.data.access.model.SessionDataDescriptor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * A label provider for a {@link SessionPage}
 */
public class SessionPageLabelProvider extends BaseLabelProvider implements
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
  public Image getColumnImage(Object element, int columnIndex) {
    if (element instanceof SessionDataDescriptor && columnIndex == 0)
      return dateLabels.getImage(((SessionDataDescriptor) element).getDate());
    else
      return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      if (element instanceof SessionDataDescriptor)
        return dateLabels.getText(((SessionDataDescriptor) element).getDate());
      else
        return null;

    case 1:
      if (element instanceof SessionDataDescriptor)
        return toDefaultString(((SessionDataDescriptor) element).getValue());
      else
        return null;

    default:
      return null;
    }
  }

  /**
   * Updates the state of this label provider, this method should be called when
   * the input of the viewer is changed.
   */
  @OverridingMethodsMustInvokeSuper
  public void updateState() {
    dateLabels.updateState();
  }
}
