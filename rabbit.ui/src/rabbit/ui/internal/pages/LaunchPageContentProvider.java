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
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.Pair;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

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
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.IdentityHashMap;

import javax.annotation.Nonnull;

/**
 * Content provider for a {@link LaunchPage}. Acceptable input is 
 * {@code Collection<LaunchDataDescriptor>}
 */
public class LaunchPageContentProvider extends AbstractCategoryContentProvider {
  
  /**
   * Categories supported by {@link LaunchPageContentProvider}
   */
  public static enum Category implements ICategory {

    /** Date category */
    DATE("Dates", SharedImages.CALENDAR),

    /** Launch category */
    LAUNCH("Launches", DebugUITools
        .getImageDescriptor(IDebugUIConstants.IMG_OBJS_DEBUG_TARGET)),

    /** Launch mode category */
    LAUNCH_MODE("Launch Modes", DebugUITools
        .getImageDescriptor(IDebugUIConstants.IMG_ACT_DEBUG)),

    /** Launch type category */
    LAUNCH_TYPE("Launch Types", DebugUITools
        .getImageDescriptor(IDebugUIConstants.IMG_OBJS_ENVIRONMENT));

    @Nonnull private final String text;
    @Nonnull private final ImageDescriptor image;

    private Category(@Nonnull String text, @Nonnull ImageDescriptor image) {
      this.text = text;
      this.image = image;
    }

    @Nonnull
    @Override
    public ImageDescriptor getImageDescriptor() {
      return image;
    }

    @Nonnull
    @Override
    public String getText() {
      return text;
    }
  }
  
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
  protected ImmutableBiMap<ICategory, Class<?>> getCategoriesAndClassesMap() {
    return ImmutableBiMap.<ICategory, Class<?>> builder()
        .put(Category.LAUNCH, Pair.class)
        .put(Category.LAUNCH_TYPE, ILaunchConfigurationType.class)
        .put(Category.LAUNCH_MODE, ILaunchMode.class)
        .put(Category.DATE, LocalDate.class).build();
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
    IFileStore store = DataHandler.getFileMapper();
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
    Class<?> clazz = categoriesAndClasses.get(getPaintCategory());
    if (clazz != null)
      return clazz.isAssignableFrom(node.getValue().getClass());
    else
      return false;
  }

  private void updateMaxValues() {
    maxLaunchDuration = TreeNodes.findMaxLong(getRoot(),
        categoriesAndClasses.get(getPaintCategory()));
    maxLaunchCount = TreeNodes.findMaxInt(getRoot(),
        categoriesAndClasses.get(getPaintCategory()));
  }
}
