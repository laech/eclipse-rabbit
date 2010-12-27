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

import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;
import rabbit.ui.internal.viewers.DateLabelProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;
import org.joda.time.LocalDate;

/**
 * Label provider for a {@link PerspectivePage}.
 */
public class PerspectivePageLabelProvider extends LabelProvider implements
    ITableLabelProvider, IColorProvider {

  private final Color gray;
  private final DateLabelProvider dateLabels;
  private final PerspectiveLabelProvider perspectiveLabels;
  private final PerspectivePageContentProvider contentProvider;

  /**
   * Constructor.
   * 
   * @param content The content provider for the page.
   * @throws NullPointerException If argument is null.
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
  public Image getImage(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    
    return (element instanceof LocalDate) ? dateLabels.getImage(element)
        : perspectiveLabels.getImage(element);
  }
  
  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();

    return (element instanceof LocalDate) ? dateLabels.getText(element)
        : perspectiveLabels.getText(element);
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return (columnIndex == 0) ? getImage(element) : null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      return getText(element);

    case 1:
      return !contentProvider.shouldPaint(element) ? null
          : format(contentProvider.getValue(element));

    default:
      return null;
    }
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
      
    if (element instanceof UndefinedPerspectiveDescriptor)
      return gray;
    else
      return null;
  }
}
