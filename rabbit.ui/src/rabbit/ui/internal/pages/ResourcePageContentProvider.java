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
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.IdentityHashMap;

import javax.annotation.Nonnull;

/**
 * Content provider for a {@link TreeViewer} that accepts input as a
 * {@link Collection} of {@link FileDataDescriptor}.
 */
public class ResourcePageContentProvider extends AbstractCategoryContentProvider
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
   * Categories supported by {@link ResourcePageContentProvider}. For
   * structuring the data.
   */
  public static enum Category implements ICategory {

    /** Date category */
    DATE("Dates", SharedImages.CALENDAR),

    /** Project category */
    PROJECT("Projects", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),

    /** Folder category */
    FOLDER("Folders", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)),

    /** File category */
    FILE("Files", PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

    private String text;
    private ImageDescriptor image;

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
   * A cached map of tree nodes and the total duration of that subtree. We use
   * an identity hash map here because two tree nodes can contain the same
   * object but still have different durations.
   * <p>
   * For example, a user worked on fileA for 10 minutes on Monday, 20 minutes on
   * Tuesday, then we can have two tree nodes containing fileA but different
   * durations (10 minutes and 20 minutes).
   * </p>
   */
  @Nonnull
  private IdentityHashMap<TreeNode, Long> treeNodeValues;

  /** {@link #getMaxValue()} */
  private long maxValue = 0;

  /**
   * Constructor a content provider for the given viewer.
   * 
   * @param viewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  public ResourcePageContentProvider(TreeViewer viewer) {
    super(viewer);
    treeNodeValues = Maps.newIdentityHashMap();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    reorganizeData((Collection<FileDataDescriptor>) newInput);
  }

  @Override
  public long getMaxValue() {
    return maxValue;
  }

  @Override
  public long getValue(Object element) {
    if (false == element instanceof TreeNode)
      return 0;

    TreeNode node = (TreeNode) element;
    Long value = treeNodeValues.get(node);
    if (value == null) {
      value = TreeNodes.longValueOfSubtree(node);
      treeNodeValues.put(node, value);
    }
    return value;
  }

  @Override
  public boolean hasChildren(Object element) {
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null || node.getChildren().length == 0) {
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
  public boolean shouldPaint(Object element) {
    if (!(element instanceof TreeNode))
      return false;
    
    TreeNode node = (TreeNode) element;
    return getCategorizers().get(getPaintCategory()).apply(node.getValue());
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return Category.values();
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

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.PROJECT;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.PROJECT, Category.FOLDER, Category.FILE };
  }

  /**
   * Reorganizes the data according to {@link #getSelectedCategories()}.
   */
  private void reorganizeData(Collection<FileDataDescriptor> data) {
    getRoot().setChildren(null);

    ICategory[] categories = getSelectedCategories();
    IFileStore store = DataHandler.getFileStore();
    for (FileDataDescriptor des : data) {

      IFile file = store.getFile(des.getFileId());
      if (file == null) {
        file = store.getExternalFile(des.getFileId());
      }
      if (file == null) {
        continue;
      }
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

    treeNodeValues.clear();
    treeNodeValues = Maps.newIdentityHashMap();
    updateMaxValue();
  }
  
  @Override
  public void setPaintCategory(ICategory cat) {
    super.setPaintCategory(cat);
    updateMaxValue();
  }

  /**
   * Updates the max value for painting.
   * @see #getMaxValue()
   */
  private void updateMaxValue() {
    maxValue = TreeNodes.findMaxLong(getRoot(), getCategorizers().get(getPaintCategory()));
  }
}