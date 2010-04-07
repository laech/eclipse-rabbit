/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.pages;

import static rabbit.ui.MillisConverter.toDefaultString;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for a {@link PerspectivePage}'s duration column.
 */
public class PerspectivePageDurationLabelProvider extends
    StyledCellLabelProvider {

  private final Color gray;
  private final PerspectivePageContentProvider content;

  /**
   * Constructor.
   * 
   * @param content The content provider for the page.
   */
  public PerspectivePageDurationLabelProvider(
      PerspectivePageContentProvider content) {

    checkNotNull(content);
    Display display = PlatformUI.getWorkbench().getDisplay();
    gray = display.getSystemColor(SWT.COLOR_DARK_GRAY);
    this.content = content;
    // Disable to let the text to be right justified:
    setOwnerDrawEnabled(false);
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    cell.setText(null);
    cell.setStyleRanges(new StyleRange[0]);
    cell.setImage(null);
    cell.setForeground(null);

    Object element = cell.getElement();
    if (element instanceof IPerspectiveDescriptor) {
      IPerspectiveDescriptor des = (IPerspectiveDescriptor) element;
      String text = toDefaultString(content.getValueOfPerspective(des));
      cell.setText(text);

      if (des instanceof UndefinedPerspectiveDescriptor)
        cell.setForeground(gray);

    } else if (element instanceof PerspectiveDataDescriptor) {
      PerspectiveDataDescriptor des = (PerspectiveDataDescriptor) element;
      cell.setText(toDefaultString(des.getValue()));

      if (content.getPerspective(des) instanceof UndefinedPerspectiveDescriptor)
        cell.setForeground(gray);

    }
  }
}
