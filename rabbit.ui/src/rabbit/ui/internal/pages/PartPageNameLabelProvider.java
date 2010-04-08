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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for providing labels for a {@link PartPage}'s name column.
 */
public class PartPageNameLabelProvider extends DateStyledCellLabelProvider {

  private final Color gray;
  private final WorkbenchPartLabelProvider partLabels;
  private final PartPageContentProvider content;

  /**
   * Constructor.
   * 
   * @param content The content provider for the page.
   */
  public PartPageNameLabelProvider(PartPageContentProvider content) {
    checkNotNull(content);

    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);

    partLabels = new WorkbenchPartLabelProvider();
    this.content = content;
  }

  @Override
  public void dispose() {
    partLabels.dispose();
    super.dispose();
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);

    Object element = cell.getElement();

    if (element instanceof IWorkbenchPartDescriptor) {
      cell.setText(partLabels.getText(element));
      cell.setImage(partLabels.getImage(element));

    } else if (element instanceof PartDataDescriptor) {
      IWorkbenchPartDescriptor des = content
          .getPart((PartDataDescriptor) element);
      cell.setText(partLabels.getText(des));
      cell.setImage(partLabels.getImage(des));
    }

    if (element instanceof UndefinedWorkbenchPartDescriptor)
      cell.setForeground(gray);
    else
      cell.setForeground(null);
  }

  /**
   * Gets the foreground color for undefined workbench parts.
   * 
   * @return The foreground color.
   */
  public Color getUndefindWorkbenchPartForeground() {
    return gray;
  }
}
