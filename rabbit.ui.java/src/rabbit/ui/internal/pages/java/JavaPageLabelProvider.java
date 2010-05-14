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
package rabbit.ui.internal.pages.java;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.ui.internal.pages.DateLabelProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

/**
 * Label provider for {@link JavaPage}
 */
public class JavaPageLabelProvider extends LabelProvider
    implements ITableLabelProvider, IColorProvider {
  
  private final DateLabelProvider dateLabels;
  private final JavaElementLabelProvider javaLabels;
  private final JavaPageContentProvider contentProvider;
  private final Color missingColor;

  /**
   * Constructs a new label provider.
   * @param contentProvider The content provider.
   */
  public JavaPageLabelProvider(JavaPageContentProvider contentProvider) {
    checkNotNull(contentProvider);
    dateLabels = new DateLabelProvider();
    javaLabels = new JavaElementLabelProvider(
        JavaElementLabelProvider.SHOW_DEFAULT | 
        JavaElementLabelProvider.SHOW_RETURN_TYPE |
        JavaElementLabelProvider.SHOW_SMALL_ICONS);
    this.contentProvider = contentProvider;
    missingColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
  }
  
  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
    javaLabels.dispose();
  }
  
  @Override
  public Color getBackground(Object element) {
    return null;
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
      if (contentProvider.shouldPaint(element)) {
        return format(contentProvider.getValue(element));
      }
      return null;
    default:
      return null;
    }
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof IJavaElement) {
      return ((IJavaElement) element).exists() ? null : missingColor;
    }
    return null;
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof IJavaElement) {
      return javaLabels.getImage(element);
    }
    return dateLabels.getImage(element);
  }
  
  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode) {
      element = ((TreeNode) element).getValue();
    }
    if (element instanceof LocalDate) {
      return dateLabels.getText(element);
    }
    return javaLabels.getText(element);
  }
}
