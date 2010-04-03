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

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for a command page.
 */
public class CommandPageLabelProvider extends LabelProvider implements
    ITableLabelProvider, IColorProvider {

  private CommandPage page;
  private final Color undefinedColor;

  /**
   * Constructs a new label provider.
   * 
   * @param page The parent.
   */
  public CommandPageLabelProvider(CommandPage page) {
    this.page = page;
    undefinedColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    if (!(element instanceof Command)) {
      return null;
    }

    Command cmd = (Command) element;
    try {
      switch (columnIndex) {
      case 0:
        return cmd.getName();
      case 1:
        return cmd.getDescription();
      case 2:
        return (int) page.getValue(cmd) + "";
      default:
        return null;
      }
    } catch (NotDefinedException e) {
      return (columnIndex == 0) ? cmd.getId() : null;
    }
  }

  @Override
  public Color getForeground(Object element) {
    if ((element instanceof Command) && !((Command) element).isDefined()) {
      return undefinedColor;
    }
    return null;
  }
}
