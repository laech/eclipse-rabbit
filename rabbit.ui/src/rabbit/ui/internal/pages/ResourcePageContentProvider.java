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

import rabbit.data.access.model.FileDataDescriptor;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Content provider for a {@link TreeViewer} that accepts input as a
 * {@link Collection} of {@link FileDataDescriptor}.
 * <p>
 * The following {@link ICategory}s are supported:
 * <ul>
 * <li>{@link Category#DATE}</li>
 * <li>{@link Category#PROJECT}</li>
 * <li>{@link Category#FOLDER}</li>
 * <li>{@link Category#FILE}</li>
 * </ul>
 * </p>
 */
public class ResourcePageContentProvider extends AbstractValueContentProvider {

  /**
   * Constructor a content provider for the given viewer.
   * 
   * @param viewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  public ResourcePageContentProvider(TreeViewer viewer) {
    super(viewer);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    Collection<FileDataDescriptor> data = null;
    try {
      data = (Collection<FileDataDescriptor>) newInput;
    } catch (Exception e) {
      return;
    }
    reorganizeData(data);
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return new ICategory[] { 
        Category.DATE, Category.PROJECT, Category.FOLDER, Category.FILE };
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.PROJECT;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.PROJECT, Category.FOLDER, Category.FILE };
  }

  @Override
  protected ImmutableBiMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableBiMap.<ICategory, Predicate<Object>> builder()
        .put(Category.PROJECT, Predicates.instanceOf(IProject.class))
        .put(Category.FOLDER,  Predicates.instanceOf(IFolder.class))
        .put(Category.FILE,    Predicates.instanceOf(IFile.class))
        .put(Category.DATE,    Predicates.instanceOf(LocalDate.class))
        .build();
  }

  /**
   * Reorganizes the data according to {@link #getSelectedCategories()}.
   */
  private void reorganizeData(Collection<FileDataDescriptor> data) {
    getRoot().setChildren(null);

    ICategory[] categories = getSelectedCategories();
    IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
    for (FileDataDescriptor des : data) {

      IPath path = des.getFilePath();
      if (path.segmentCount() < 2) {
        continue; // Invalid path.
      }
      
      String pathStr = path.toString();
      if (pathStr.contains(":")) {
        path = new Path(pathStr.replace(":", ""));
      }
      IFile file = workspace.getFile(path);
      IProject project = file.getProject();
      IContainer folder = file.getParent();

      TreeNode node = getRoot();
      for (ICategory cat : categories) {
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.PROJECT == cat) {
          node = TreeNodes.findOrAppend(node, project);
          
        } else if (Category.FOLDER == cat) {
          if (!folder.equals(project))
            node = TreeNodes.findOrAppend(node, folder);
          
        } else if (Category.FILE == cat) {
          node = TreeNodes.findOrAppend(node, file);
        }
      }
      TreeNodes.appendToParent(node, des.getValue());
    }
  }
}