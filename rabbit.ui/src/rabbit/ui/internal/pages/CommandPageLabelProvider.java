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

import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for a {@link CommandPage}.
 */
public class CommandPageLabelProvider extends LabelProvider implements
    ITableLabelProvider, IColorProvider {

  private final Color gray; 
  private final IValueProvider values;
  private final DateLabelProvider dateLabels;
  private final CommandLabelProvider commandLabels;
  private final WorkspaceStorageLabelProvider workspaceLabels;

  /**
   * Constructor.
   * 
   * @param valueProvider The value provider to provide execution counts.
   * @throws NullPointerException If argument is null.
   */
  public CommandPageLabelProvider(IValueProvider valueProvider) {
    values = checkNotNull(valueProvider);
    dateLabels = new DateLabelProvider();
    commandLabels = new CommandLabelProvider();
    workspaceLabels = new WorkspaceStorageLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
    commandLabels.dispose();
    workspaceLabels.dispose();
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
      if (element instanceof TreeNode) {
        Object value = ((TreeNode) element).getValue();
        if (value instanceof Command) {
          try {
            return ((Command) value).getDescription();
          } catch (NotDefinedException e) {
            return null;
          }
        }
      }
      return null;

    case 2:
      if (values.shouldPaint(element))
        return String.valueOf(values.getValue(element));
      else
        return null;

    default:
      return null;
    }

  }
  
  @Override
  public Color getForeground(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    if ((element instanceof Command) && !((Command) element).isDefined())
      return gray;
    if (element instanceof WorkspaceStorage)
      return workspaceLabels.getForeground(element);
    else
      return null;
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    if (element instanceof Command)
      return commandLabels.getImage(element);
    if (element instanceof WorkspaceStorage)
      return workspaceLabels.getImage(element);
    else
      return dateLabels.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    if (element instanceof Command)
      return commandLabels.getText(element);
    if (element instanceof WorkspaceStorage)
      return workspaceLabels.getText(element);
    else
      return dateLabels.getText(element);
  }
}