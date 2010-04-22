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

import rabbit.ui.internal.pages.ResourcePageLabelProvider;
import rabbit.ui.internal.util.UnrecognizedTask;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for {@link TaskPage}
 */
public class TaskPageLabelProvider extends LabelProvider implements
    IColorProvider, IFontProvider {

  private final ResourcePageLabelProvider resourceProvider;
  private final TaskElementLabelProvider taskProvider;
  private final Color gray;

//  private final Image missingCategoryImg;
  private final Image missingTaskImg;

  /**
   * Constructs a new label provider.
   */
  public TaskPageLabelProvider() {
//    missingCategoryImg = TasksUiImages.CATEGORY.createImage();
    missingTaskImg = TasksUiImages.TASK.createImage();
    taskProvider = new TaskElementLabelProvider();
    resourceProvider = new ResourcePageLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
//    missingCategoryImg.dispose();
    missingTaskImg.dispose();
    taskProvider.dispose();
    resourceProvider.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Font getFont(Object element) {
    if (element instanceof TreeNode)
      return taskProvider.getFont(((TreeNode) element).getValue());
    
    return null;
  }

  @Override
  public Color getForeground(Object element) {
    if (!(element instanceof TreeNode))
      return null;
    
    Object value = ((TreeNode) element).getValue();
    if (value instanceof UnrecognizedTask)
      return gray;
    
    if (value instanceof ITask)
      return taskProvider.getForeground(value);
    
    return resourceProvider.getForeground(element);
  }

  @Override
  public Image getImage(Object element) {
    if (!(element instanceof TreeNode))
      return super.getImage(element);
    
    Object value = ((TreeNode) element).getValue();
    if (value instanceof UnrecognizedTask)
      return missingTaskImg;

    if (value instanceof ITask)
      return taskProvider.getImage(value);
    
    return resourceProvider.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (!(element instanceof TreeNode))
      return super.getText(element);
    
    Object value = ((TreeNode) element).getValue();
    if (value instanceof ITask)
      return taskProvider.getText(value);
    
    return resourceProvider.getText(element);
  }
}
