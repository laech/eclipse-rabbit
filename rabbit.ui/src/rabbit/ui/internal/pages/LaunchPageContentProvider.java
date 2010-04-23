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
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.Pair;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * Acceptable input for this content provider is {@code
 * Collection<LaunchDataDescriptor>}
 * <p>
 * The following {@link ICategory}s are supported:
 * <ul>
 * <li>{@link Category#DATE}</li>
 * <li>{@link Category#LAUNCH}</li>
 * <li>{@link Category#LAUNCH_MODE}</li>
 * <li>{@link Category#LAUNCH_TYPE}</li>
 * </ul>
 * </p>
 */
public class LaunchPageContentProvider extends AbstractCategoryContentProvider {

  /*
   * This content provider builds a tree from the input data, and every leaf
   * node of the tree is containing a java.util.Long value, or a 
   * java.util.Integer value, these values are the values of each 
   * LaunchDataDescriptor. This way, we can calculate the total value of every 
   * subtree by traversal. If the data descriptor contains resources, a subtree
   * of the resources will also be attached to the same parent as the Long &
   * Integer nodes, for example:
   * 
   * (OtherParentNodes) +-- Integer
   *                    |
   *                    +-- Long
   *                    |
   *                    | // If there are files, one more subtree will be here:
   *                    +-- (Project) +-- (Folder) --- (File)
   *                                  |
   *                                  +-- (File)
   * 
   */
  
  /**
   * Value provider for launch count values.
   * @see LaunchDataDescriptor#getLaunchCount()
   */
  private final IValueProvider countValueProvider = new IValueProvider() {

    @Override
    public long getMaxValue() {
      return maxLaunchCount;
    }

    @Override
    public long getValue(Object element) {
      if (!(element instanceof TreeNode))
        return 0;

      Integer count = launchCounts.get(element);
      if (count == null) {
        count = TreeNodes.intValueOfSubtree((TreeNode) element);
        launchCounts.put((TreeNode) element, count);
      }
      return count;
    }

    @Override
    public boolean shouldPaint(Object element) {
      return LaunchPageContentProvider.this.shouldPaint(element);
    }
  };

  /**
   * Value provider for launch duration values.
   * @see LaunchDataDescriptor#getTotalDuration()
   */
  private final IValueProvider durationValueProvider = new IValueProvider() {

    @Override
    public long getMaxValue() {
      return maxLaunchDuration;
    }

    @Override
    public long getValue(Object element) {
      if (!(element instanceof TreeNode))
        return 0;

      Long duration = launchDurations.get(element);
      if (duration == null) {
        duration = TreeNodes.longValueOfSubtree((TreeNode) element);
        launchDurations.put((TreeNode) element, duration);
      }
      return duration;
    }

    @Override
    public boolean shouldPaint(Object element) {
      return LaunchPageContentProvider.this.shouldPaint(element);
    }
  };

  /**
   * Max value for {@link #countValueProvider}
   */
  private int maxLaunchCount;

  /**
   * Max value for {@link #durationValueProvider}
   */
  private long maxLaunchDuration;
  
  /**
   * Map containing cached value of each tree node and the launch count of that 
   * node. This map must be cleared when the viewer's input changes.
   */
  private IdentityHashMap<TreeNode, Integer> launchCounts;
  
  /**
   * Map containing cached value of each tree node and the launch duration of 
   * that node. This map must be cleared when the viewer's input changes.
   */
  private IdentityHashMap<TreeNode, Long> launchDurations;

  /**
   * Constructs a new content provider.
   * @param viewer The viewer this content provider is for.
   * @throws NullPointerException If viewer is null.
   */
  public LaunchPageContentProvider(TreeViewer viewer) {
    super(viewer);
    launchCounts = Maps.newIdentityHashMap();
    launchDurations = Maps.newIdentityHashMap();
  }

  /**
   * Gets the value provider for launch count values.
   * @return A value provider.
   * @see LaunchDataDescriptor#getLaunchCount()
   */
  public IValueProvider getLaunchCountValueProvider() {
    return countValueProvider;
  }
  
  /**
   * Gets the value provider for launch duration values.
   * @return A value provider.
   * @see LaunchDataDescriptor#getTotalDuration()
   */
  public IValueProvider getLaunchDurationValueProvider() {
    return durationValueProvider;
  }

  @Override
  public boolean hasChildren(Object element) {
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null || node.getChildren().length == 0)
      return false;

    /*
     * Hides the pure numeric tree nodes, these are nodes with java.lang.Long
     * objects or java.lang.Integer objects hanging at the end of the branches, 
     * we only use those to calculate values for the tree nodes, not to be shown 
     * to the users:
     */
    for (TreeNode child : node.getChildren()) {
      if (!(child.getValue() instanceof Integer)
          && !(child.getValue() instanceof Long))
        return true;
    }

    return false;
  }

  @Override
  public void setPaintCategory(ICategory cat) {
    super.setPaintCategory(cat);
    updateMaxValues();
  }

  @Override
  public boolean shouldFilter(Object element) {
    if (element instanceof TreeNode) {
      TreeNode node = (TreeNode) element;
      if (node.getValue() instanceof IResource)
        return false;
    }

    return super.shouldFilter(element);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    reorganizeData((Collection<LaunchDataDescriptor>) newInput);
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return Category.values();
  }

  @Override
  protected ImmutableBiMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableBiMap.<ICategory, Predicate<Object>> builder()
        .put(Category.LAUNCH,      Predicates.instanceOf(Pair.class))
        .put(Category.LAUNCH_TYPE, Predicates.instanceOf(ILaunchConfigurationType.class))
        .put(Category.LAUNCH_MODE, Predicates.instanceOf(ILaunchMode.class))
        .put(Category.DATE,        Predicates.instanceOf(LocalDate.class))
        .build();
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.LAUNCH;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.LAUNCH };
  }

  /**
   * Reorganizes the data according to {@link #getSelectedCategories()}.
   */
  private void reorganizeData(Collection<LaunchDataDescriptor> data) {
    getRoot().setChildren(null);

    ICategory[] categories = getSelectedCategories();
    IFileStore store = DataHandler.getFileStore();
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    for (LaunchDataDescriptor des : data) {

      TreeNode launchNode = getRoot();
      for (ICategory cat : categories) {
        if (Category.DATE == cat) {
          launchNode = TreeNodes.findOrAppend(launchNode, des.getDate());

        } else if (Category.LAUNCH == cat) {
          LaunchConfigurationDescriptor config = des.getLaunchDescriptor();
          launchNode = TreeNodes.findOrAppend(launchNode,
              Pair.create(config.getLaunchName(), config.getLaunchTypeId()));

        } else if (Category.LAUNCH_TYPE == cat) {
          String id = des.getLaunchDescriptor().getLaunchTypeId();
          ILaunchConfigurationType tp = manager.getLaunchConfigurationType(id);
          if (tp == null)
            tp = new UndefinedLaunchConfigurationType(id);

          launchNode = TreeNodes.findOrAppend(launchNode, tp);

        } else if (Category.LAUNCH_MODE == cat) {
          String modeId = des.getLaunchDescriptor().getLaunchModeId();
          ILaunchMode mode = manager.getLaunchMode(modeId);
          if (mode == null)
            mode = new UndefinedLaunchMode(modeId);

          launchNode = TreeNodes.findOrAppend(launchNode, mode);
        }
      }

      // Attach the Integer value of launch count, and Long value of the total
      // duration count at the end:
      TreeNodes.appendToParent(launchNode, des.getLaunchCount());
      TreeNodes.appendToParent(launchNode, des.getTotalDuration());

      // Adds the resources (if any) to the end of the leaf:
      for (String fileId : des.getFileIds()) {
        TreeNode resourceNode = launchNode;
        IFile file = store.getFile(fileId);
        if (file == null)
          file = store.getExternalFile(fileId);
        if (file == null)
          continue;
        IProject project = file.getProject();
        IContainer folder = file.getParent();

        resourceNode = TreeNodes.findOrAppend(resourceNode, project);
        if (!folder.equals(project))
          resourceNode = TreeNodes.findOrAppend(resourceNode, folder);

        resourceNode = TreeNodes.findOrAppend(resourceNode, file);
      }
    }

    launchCounts.clear();
    launchDurations.clear();
    updateMaxValues();
  }

  private boolean shouldPaint(Object element) {
    if (!(element instanceof TreeNode))
      return false;

    TreeNode node = (TreeNode) element;
    return getCategorizers().get(getPaintCategory()).apply(node.getValue());
  }

  private void updateMaxValues() {
    maxLaunchDuration = TreeNodes.findMaxLong(getRoot(),
        getCategorizers().get(getPaintCategory()));
    maxLaunchCount = TreeNodes.findMaxInt(getRoot(),
        getCategorizers().get(getPaintCategory()));
  }
}
