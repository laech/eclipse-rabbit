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

import rabbit.data.IFileStore;
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.pages.AbstractCategoryContentProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UnrecognizedTask;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import java.util.IdentityHashMap;

/**
 * Content provider accepts input as {@code Collection<TaskFileDataDescriptor}
 */
public class TaskPageContentProvider extends AbstractCategoryContentProvider
    implements IValueProvider {

  /*
   * This content provider builds a tree from the input data, and every leaf
   * node of the tree is containing a java.util.Long value, these values are the
   * values of each FileDataDescriptor. This way, we can calculate the total 
   * value of every subtree by traversal. For example:
   * 
   * Parent +-- Child1 --- Long
   *        |
   *        +-- Child2 --- Long
   *        |
   *        +-- Child3 --- Long
   */
  
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

  private long maxValue;

  private IdentityHashMap<TreeNode, Long> treeNodeValues = Maps.newIdentityHashMap();

  public TaskPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
  }

  @Override
  public long getMaxValue() {
    return maxValue;
  }

  @Override
  public long getValue(Object element) {
    if (element instanceof TreeNode) {
      TreeNode node = (TreeNode) element;
      Long value = treeNodeValues.get(node);
      if (value == null) {
        value = TreeNodes.longValueOfSubtree(node);
        treeNodeValues.put(node, value);
      }
      return value;
    }
    return 0;
  }

  @Override
  public boolean hasChildren(Object element) {
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null) {
      return false;
    }

    /*
     * Hides the pure numeric tree nodes, these are nodes with java.lang.Long
     * objects hanging at the end of the branches, we only use those to
     * calculate values for the parents, not to be shown to the users:
     */
    if (node.getChildren()[0].getValue() instanceof Long) {
      return false;
    }

    return true;
  }
  
  @Override
  public void setPaintCategory(ICategory cat) {
    super.setPaintCategory(cat);
    updateMaxValue();
  }
  
  @Override
  public boolean shouldPaint(Object element) {
    if (!(element instanceof TreeNode))
      return false;
    
    TreeNode node = (TreeNode) element;
    return getCategorizers().get(getPaintCategory()).apply(node.getValue());
  }
  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    reorganizeData((Collection<TaskFileDataDescriptor>) newInput);
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
    IFileStore fileStore = DataHandler.getFileStore();
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
          IFile file = fileStore.getFile(des.getFileId());
          if (file == null)
            file = fileStore.getExternalFile(des.getFileId());
          if (file == null)
            continue;

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
    updateMaxValue();
  }
  
  private void updateMaxValue() {
    maxValue = TreeNodes.findMaxLong(getRoot(), 
        getCategorizers().get(getPaintCategory()));
  }
}