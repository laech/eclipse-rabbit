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

import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UnrecognizedTask;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Content provider accepts input as {@code Collection<TaskFileDataDescriptor}
 */
public class TaskPageContentProvider extends AbstractValueContentProvider {
  
  /**
   * TODO
   */
  public enum Category implements ICategory {

    /***/
    FILE("Files", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE)),
    /***/
    FOLDER("Folders", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)),
    /***/
    PROJECT("Projects", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),
    /***/
    TASK("Tasks", TasksUiImages.TASK),
    /***/
    DATE("Dates", SharedImages.CALENDAR);

    private final String text;
    private final ImageDescriptor image;
    
    private Category(String text, ImageDescriptor image) {
      this.text = text;
      this.image = image;
    }
    
    @Override
    public ImageDescriptor getImageDescriptor() {
      return image;
    }
    
    @Override
    public String getText() {
      return text;
    }
  }

  /**
   * Constructs a new content provider for the given viewer.
   * @param treeViewer The viewer of this content provider.
   */
  public TaskPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    
    Collection<TaskFileDataDescriptor> data = null;
    try {
      data = (Collection<TaskFileDataDescriptor>) newInput;
    } catch (Exception e) {
      return;
    }
    reorganizeData(data);
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return Category.values();
  }
  
  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.TASK;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.TASK, 
                             Category.PROJECT, 
                             Category.FOLDER, 
                             Category.FILE };
  }

  @Override
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableBiMap.<ICategory, Predicate<Object>> builder()
        .put(Category.DATE,    Predicates.instanceOf(LocalDate.class))
        .put(Category.TASK,    Predicates.instanceOf(ITask.class))
        .put(Category.PROJECT, Predicates.instanceOf(IProject.class))
        .put(Category.FOLDER,  Predicates.instanceOf(IFolder.class))
        .put(Category.FILE,    Predicates.instanceOf(IFile.class))
        .build();
  }
  
  private void reorganizeData(Collection<TaskFileDataDescriptor> data) {
    getRoot().setChildren(null);
    
    IRepositoryModel repo = TasksUi.getRepositoryModel();
    IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
    for (TaskFileDataDescriptor des : data) {
      
      TreeNode node = getRoot();
      for (ICategory cat : selectedCategories) {
        
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.TASK == cat) {
          TaskId id = des.getTaskId();
          ITask task = repo.getTask(id.getHandleIdentifier());
          if (task != null && !id.getCreationDate().equals(task.getCreationDate()))
            task = null;
          if (task == null)
            task = new UnrecognizedTask(id);
          
          node = TreeNodes.findOrAppend(node, task);
          
        } else { // PROJECT/FOLDER/FILE Categories:
          
          IPath path = des.getFilePath();
          if (path.segmentCount() < 2) {
            continue; // Invalid path.
          }
          
          String pathStr = path.toString();
          if (pathStr.contains(":")) {
            path = new Path(pathStr.replace(":", ""));
          }
          IFile file = workspace.getFile(path);

          if (Category.PROJECT == cat)
            node = TreeNodes.findOrAppend(node, file.getProject());
          
          else if (Category.FOLDER == cat 
              && file.getFullPath().segmentCount() > 2) // Parent != Project
            node = TreeNodes.findOrAppend(node, file.getParent());
          
          else if (Category.FILE == cat)
            node = TreeNodes.findOrAppend(node, file);
        }
      }
      
      TreeNodes.appendToParent(node, des.getValue());
    }
  }
}