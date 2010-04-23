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

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract content provider supports categorization of data.
 * 
 * This class uses {@link TreeNode} to build a tree model from the data. Each
 * value of each {@code TreeNode} can be put into an {@link ICategory}, each
 * category also represents a unique class type. For example, if there is a
 * category called "File", then the corresponding class type of this category
 * can be {@code IFile.class}, all tree nodes who contain objects of type
 * {@code IFile} can be put into the "File" category.
 */
public abstract class AbstractCategoryContentProvider extends
    TreeNodeContentProvider implements ICategoryProvider {

  /**
   * An unmodifiable set of all the categories supported by this content
   * provider.
   */
  protected final Set<ICategory> allCategories;
  
  /**
   * An unmodifiable map contain categories and predicates to categorize the 
   * elements.
   */
  private final Map<ICategory, Predicate<Object>> categorizers;

  /**
   * A set of selected categories, the data will be structured using these
   * categories. The order and uniqueness are important, so this is a
   * {@link LinkedHashSet}.
   */
  protected final Set<ICategory> selectedCategories;

  private ICategory paintCategory;

  /** The root of the content tree. */
  private final TreeNode root;

  private final TreeViewer viewer;

  /**
   * Constructor.
   * @param treeViewer The viewer of this content provider.
   * @throws NullPointerException If argument is null.
   */
  public AbstractCategoryContentProvider(TreeViewer treeViewer) {
    checkNotNull(treeViewer);
    viewer = treeViewer;
    root = new TreeNode(new Object());
    allCategories = ImmutableSet.of(getAllSupportedCategories());

    selectedCategories = Sets.newLinkedHashSet();
    for (ICategory category : getDefaultSelectedCategories())
      selectedCategories.add(category);

    categorizers = initializeCategorizers();
    paintCategory = getDefaultPaintCategory();
  }

  /**
   * Gets the map contain categories and predicates to categorize
   * the elements.
   * @return The map, unmodifiable.
   */
  public Map<ICategory, Predicate<Object>> getCategorizers() {
    return categorizers;
  }
  
  @Override
  public Object[] getElements(Object inputElement) {
    return (getRoot().getChildren() != null) ? getRoot().getChildren()
        : new Object[0];
  }

  /**
   * Gets the category that is currently used to identify elements for painting.
   * 
   * @return The paint category.
   */
  public ICategory getPaintCategory() {
    return paintCategory;
  }

  /**
   * Gets the root of the content tree.
   * @return The root.
   */
  public TreeNode getRoot() {
    return root;
  }

  @Override
  public ICategory[] getSelectedCategories() {
    return selectedCategories.toArray(new ICategory[selectedCategories.size()]);
  }

  @Override
  public ICategory[] getUnselectedCategories() {
    Set<ICategory> set = Sets.difference(allCategories, selectedCategories);
    return set.toArray(new ICategory[set.size()]);
  }

  /**
   * Gets the viewer of this content provider.
   * @return The viewer.
   */
  public TreeViewer getViewer() {
    return viewer;
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);
    if (newInput != null)
      doInputChanged(viewer, oldInput, newInput);
  }

  /**
   * Sets which category is to be painted.
   * 
   * @param cat The category.
   */
  public void setPaintCategory(ICategory cat) {
    if (paintCategory != cat && allCategories.contains(cat)) {
      paintCategory = cat;
      getViewer().refresh();
    } else {
      return;
    }
  }

  @Override
  public void setSelectedCategories(ICategory... categories) {
    // Restore the defaults if the categories array is null or empty:
    if (categories == null || categories.length == 0)
      categories = getDefaultSelectedCategories();

    if (Arrays.equals(categories, selectedCategories.toArray()))
      return; // Nothing to do if same.

    selectedCategories.clear();
    for (ICategory category : categories) {
      if (allCategories.contains(category))
        selectedCategories.add(category);
    }

    Object[] elements = getViewer().getExpandedElements();
    // Resets the input instead of calling refresh, ensures the data is
    // correctly structured:
    getViewer().setInput(getViewer().getInput());
    try {
      getViewer().setExpandedElements(elements);
    } catch (Exception e) {
      // Just in case some elements are no valid.
    }
  }

  /**
   * Checks whether the given element should be hidden by the current structure.
   * 
   * @param element The element.
   * @return True if the element should be hidden, false otherwise.
   */
  public boolean shouldFilter(Object element) {
    if (!(element instanceof TreeNode))
      return true;

    TreeNode node = (TreeNode) element;
    for (ICategory cat : selectedCategories) {
      Predicate<Object> predicate = getCategorizers().get(cat);
      if (predicate != null && predicate.apply(node.getValue()))
        return false;
    }
    return true;
  }

  /**
   * Notifies the subclasses that the input has been changed. Subclasses should
   * override this method.
   * 
   * @param viewer The viewer.
   * @param oldInput The old input, may be null.
   * @param newInput The new input, not null.
   */
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }

  /**
   * Gets all the categories supported by this content provider.
   * @return All the categories supported by this content provider.
   */
  protected abstract ICategory[] getAllSupportedCategories();

  /**
   * Gets the default paint category.
   * 
   * @return The default paint category.
   */
  protected abstract ICategory getDefaultPaintCategory();

  /**
   * Gets the default selected categories of this content provider.
   * @return An ordered array of default category selection.
   */
  protected abstract ICategory[] getDefaultSelectedCategories();

  /**
   * Gets the mapping of categories and classes defined by subclasses.
   * @return The mapping of categories and classes.
   */
  protected abstract ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers();
}
