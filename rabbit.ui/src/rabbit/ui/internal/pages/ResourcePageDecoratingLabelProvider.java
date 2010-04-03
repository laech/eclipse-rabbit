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

import rabbit.ui.internal.pages.ResourcePage.ShowMode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ResourcePageDecoratingLabelProvider extends
    DecoratingLabelProvider implements ITableLabelProvider {

  private ResourcePage page;

  public ResourcePageDecoratingLabelProvider(ResourcePage parent,
      ILabelProvider provider, ILabelDecorator decorator) {
    super(provider, decorator);
    page = parent;
  }

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    if (columnIndex == 0) {
      return super.getImage(element);
    }
    return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    switch (columnIndex) {
    case 0:
      if (element instanceof IFile) {
        return ((IFile) element).getName();
      }
      return super.getText(element);
    case 1:
      if (!(element instanceof IResource)) {
        return null;
      }
      IResource resource = (IResource) element;
      if ((resource instanceof IProject && page.getShowMode() == ShowMode.PROJECT)
          || (resource instanceof IFolder && page.getShowMode() == ShowMode.FOLDER)
          || (resource instanceof IFile && page.getShowMode() == ShowMode.FILE)) {

        return toDefaultString(page.getValue(resource));
      }
    default:
      return null;
    }
  }
}