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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Label provider for a {@link PerspectivePage}.
 */
public class PerspectivePageLabelProvider extends BaseLabelProvider implements
    ITableLabelProvider, IColorProvider {

  private final Color gray;
  private final DateLabelProvider dateLabels;
  private final PerspectiveLabelProvider perspectiveLabels;
  private final PerspectivePageContentProvider contentProvider;

  /**
   * Constructor.
   * 
   * @param content The content provider for the page.
   */
  public PerspectivePageLabelProvider(PerspectivePageContentProvider content) {
    checkNotNull(content);

    contentProvider = content;
    dateLabels = new DateLabelProvider();
    perspectiveLabels = new PerspectiveLabelProvider(false);
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    perspectiveLabels.dispose();
    dateLabels.dispose();
    super.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    if (columnIndex != 0)
      return null;

    else if (element instanceof IPerspectiveDescriptor)
      return perspectiveLabels.getImage(element);

    else if (element instanceof PerspectiveDataDescriptor)
      return perspectiveLabels.getImage(contentProvider
          .getPerspective((PerspectiveDataDescriptor) element));

    else
      return dateLabels.getImage(element);
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      if (element instanceof IPerspectiveDescriptor)
        return perspectiveLabels.getText(element);

      else if (element instanceof PerspectiveDataDescriptor)
        return perspectiveLabels.getText(contentProvider
            .getPerspective((PerspectiveDataDescriptor) element));
      else
        return dateLabels.getText(element);

    case 1:
      if (element instanceof IPerspectiveDescriptor)
        return toDefaultString(contentProvider
            .getValueOfPerspective((IPerspectiveDescriptor) element));

      else if (element instanceof PerspectiveDataDescriptor)
        return toDefaultString(((PerspectiveDataDescriptor) element).getValue());
      else
        return null;

    default:
      return null;
    }
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof UndefinedPerspectiveDescriptor)
      return gray;

    else if (element instanceof PerspectiveDataDescriptor) {
      IPerspectiveDescriptor p = contentProvider
          .getPerspective((PerspectiveDataDescriptor) element);
      if (p instanceof UndefinedPerspectiveDescriptor)
        return gray;
    }

    return null;
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
