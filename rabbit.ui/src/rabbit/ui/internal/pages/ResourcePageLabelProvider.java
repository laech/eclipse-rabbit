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

import rabbit.ui.internal.viewers.TreeNodes;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Providers names for the elements in a {@link ResourcePage}.
 */
public class ResourcePageLabelProvider extends LabelProvider implements
    IColorProvider {

  private final Color gray;
  private final DateLabelProvider dateLabels;
  private final WorkbenchLabelProvider workbenchLabels;

  /**
   * Constructor.
   */
  public ResourcePageLabelProvider() {
    dateLabels = new DateLabelProvider();
    workbenchLabels = new WorkbenchLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    dateLabels.dispose();
    workbenchLabels.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }

  @Override
  public Color getForeground(Object element) {
    Object value = TreeNodes.getObject(element);
    if (value instanceof IResource && !((IResource) value).exists()) {
      return gray;
    }
    return null;
  }

  @Override
  public Image getImage(Object element) {
    Object value = TreeNodes.getObject(element);
    if (value instanceof IResource) {
      return workbenchLabels.getImage(value);
    }
    return dateLabels.getImage(value);
  }

  @Override
  public String getText(Object element) {
    Object value = TreeNodes.getObject(element);
    if (value instanceof IResource) {
      if (value instanceof IFolder) {
        return ((IFolder) value).getProjectRelativePath().toString();
      }
      return workbenchLabels.getText(value);
    }
    return dateLabels.getText(value);
  }
}