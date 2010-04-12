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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Label provider for a {@link PartPage}
 */
public class PartPageLabelProvider extends BaseLabelProvider implements
    ITableLabelProvider, IColorProvider {

  private final WorkbenchPartLabelProvider partLabels;
  private final PartPageContentProvider contents;
  private final DateLabelProvider dateLabels;
  private final Color gray;

  /**
   * Constructor.
   * 
   * @param contentProvider The content provider of the page.
   */
  public PartPageLabelProvider(PartPageContentProvider contentProvider) {
    contents = contentProvider;
    partLabels = new WorkbenchPartLabelProvider();
    dateLabels = new DateLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    partLabels.dispose();
    dateLabels.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Image getColumnImage(Object object, int columnIndex) {
    if (columnIndex != 0)
      return null;

    else if (object instanceof IWorkbenchPartDescriptor)
      return partLabels.getImage(object);

    else if (object instanceof PartDataDescriptor)
      return partLabels.getImage(contents.getPart((PartDataDescriptor) object));

    else
      return dateLabels.getImage(object);
  }

  @Override
  public String getColumnText(Object obj, int columnIndex) {
    switch (columnIndex) {
    case 0:
      if (obj instanceof IWorkbenchPartDescriptor)
        return partLabels.getText(obj);

      else if (obj instanceof PartDataDescriptor)
        return partLabels.getText(contents.getPart((PartDataDescriptor) obj));

      else
        return dateLabels.getText(obj);

    case 1:
      if (obj instanceof IWorkbenchPartDescriptor)
        return toDefaultString(contents
            .getValueOfPart((IWorkbenchPartDescriptor) obj));

      else if (obj instanceof PartDataDescriptor)
        return toDefaultString(((PartDataDescriptor) obj).getValue());

      else
        return null;

    default:
      return null;
    }
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof UndefinedWorkbenchPartDescriptor)
      return gray;

    else if (element instanceof PartDataDescriptor) {
      IWorkbenchPartDescriptor part = contents
          .getPart((PartDataDescriptor) element);
      if (part instanceof UndefinedWorkbenchPartDescriptor)
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
