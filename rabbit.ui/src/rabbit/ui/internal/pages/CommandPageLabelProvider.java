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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Label provider for a {@link CommandPage}.
 */
public class CommandPageLabelProvider extends LabelProvider implements
    ITableLabelProvider, IColorProvider {

  private final Color gray;
  private final DateLabelProvider dateLabels;
  private final CommandLabelProvider commandLabels;
  private final CommandPageContentProvider contentProvider;

  /**
   * Constructor.
   * 
   * @param contents The content provider of the page.
   * @throws NullPointerException If argument is null.
   */
  public CommandPageLabelProvider(CommandPageContentProvider contents) {
    checkNotNull(contents);
    contentProvider = contents;
    dateLabels = new DateLabelProvider();
    commandLabels = new CommandLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
    
    contents.getViewer().getTree().addListener(SWT.SetData, new Listener() {
      
      @Override
      public void handleEvent(Event event) {
        System.out.println("setdata");
      }
    });
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
    commandLabels.dispose();
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
      if (contentProvider.shouldPaint(element))
        return String.valueOf(contentProvider.getValue(element));
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
    else
      return null;
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    if (element instanceof Command)
      return commandLabels.getImage(element);
    else
      return dateLabels.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof TreeNode)
      element = ((TreeNode) element).getValue();
    if (element instanceof Command)
      return commandLabels.getText(element);
    else
      return dateLabels.getText(element);
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
