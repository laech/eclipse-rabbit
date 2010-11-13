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
package rabbit.ui.internal.viewers;

import rabbit.ui.internal.viewers.FilterableContentProvider;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @see FilterableContentProvider
 */
public class FilterableContentProviderTest {

  /*
   * This class tests the functionalities of FilterableContentProvider, not its
   * subclasses, which is OK as the method in FilterableContentProvider are
   * final, cannot be overridden.
   */
  
  /**
   * Helper class for building a {@link FilterableContentProvider} for testing.
   */
  private static class Builder {

    final Map<TreePath, Object[]> data = Maps.newHashMap();
    final Set<Predicate<?>> filters = Sets.newHashSet();
    Object input = new Object();
    private Object[] inputElements = new Object[0];

    /** Constructor. */
    Builder() {
    }

    /**
     * Builds a {@link FilterableContentProvider} using the configurations set
     * in this object.
     * 
     * @return A {@link FilterableContentProvider}.
     */
    FilterableContentProvider build() {
      return new MyContentProvider(filters, data, input, inputElements);
    }

    /**
     * Adds the given filter to the collection of filters for building the
     * content provider.
     * 
     * @param filter
     *          The filter to add.
     * @return {@code this}
     */
    Builder filter(Predicate<?> filter) {
      filters.add(filter);
      return this;
    }

    /**
     * Maps the given input to the given elements. When
     * {@code getElements(input)} is called on the content provider built by
     * this object, {@code children} will be returned.
     * 
     * @param input
     *          The input element.
     * @param elements
     *          The elements of the input.
     * @return {@code this}
     */
    Builder input(Object input, Object[] elements) {
      this.input = input;
      this.inputElements = elements;
      return this;
    }

    /**
     * Maps the given parent to the given children. When
     * {@code getChildren(parent} is called on the content provider built by
     * this object, {@code children} will be returned.
     * 
     * @param parentsetup
     *          The parent path.
     * @param children
     *          The children of the parent path.
     * @return {@code this}
     */
    Builder map(TreePath parent, Object[] children) {
      data.put(parent, children);
      return this;
    }
  }

  /**
   * A content provider to help testing.
   */
  private static class MyContentProvider extends FilterableContentProvider {
    
    final Map<TreePath, Object[]> data;
    final Object input;
    final List<Object> inputElements;
    
    @SuppressWarnings("unchecked")
    MyContentProvider(Collection<Predicate<?>> elementFilters,
                      Map<TreePath, Object[]> childrenByParent, 
                      Object input,
                      Object[] inputElements) {
      this.data = ImmutableMap.copyOf(childrenByParent);
      this.input = input;
      this.inputElements = ImmutableList.of(inputElements);
      for (Predicate<?> filter : elementFilters) {
        addFilter((Predicate<Object>) filter);
      }
    }

    @Override
    public void dispose() {
    }

    @Override
    public TreePath[] getParents(Object element) {
      return new TreePath[0];
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    protected Object[] doGetChildren(TreePath parentPath) {
      Object[] children = data.get(parentPath);
      return (children == null) ? EMPTY_ARRAY : children;
    }

    @Override
    protected Object[] doGetElements(Object theInput) {
      return Objects.equal(input, theInput) 
          ? inputElements.toArray()
          : EMPTY_ARRAY;
    }
  }

  private FilterableContentProvider provider;

  @Before
  public void before() {
    provider = new Builder().build();
  }

  @Test
  public void getChildrenShouldRetainDuplicateChildrenWhenFiltering() {
    Object[] initialChildren = {0, 0, "a"};
    Object[] expectedChildren = {0, 0};

    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, initialChildren)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getChildrenShouldRetainDuplicateChildrenWhenNoFilterIsUsed() {
    Object[] children = {0, 0, 0};
    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, children).build();
    assertThat(p.getChildren(parent), equalTo(children));
  }

  @Test
  public void getChildrenShouldRetainTheOrderOfTheChildrenWhenFiltering() {
    Object[] initialChildren = {3, "2", 1};
    Object[] expectedChildren = {3, 1};

    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, initialChildren)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getChildrenShouldRetainTheOrderOfTheChildrenWhenNoFilterIsUsed() {
    Object[] expectedChildren = {9, 29, "adf", 0};
    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, expectedChildren)
        .build();
    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getChildrenShouldReturnAllChildrenIfNoFilterIsUsed() {
    Object[] expectedChildren1 = {158, 290};
    Object[] expectedChildren2 = {"1", "2"};

    TreePath parent1 = newPath(10);
    TreePath parent2 = newPath("");
    FilterableContentProvider f = new Builder().map(parent1, expectedChildren1)
        .map(parent2, expectedChildren2).build();

    assertThat(f.getChildren(parent1), equalTo(expectedChildren1));
    assertThat(f.getChildren(parent2), equalTo(expectedChildren2));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayIfAllChildrenAreFilteredOut() {
    Object[] initialChildren = {1, "2", 3l};
    Object[] expectedChildren = {};

    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, initialChildren)
        .filter(instanceOf(Object.class)).build();

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayIfParentIsNull() {
    assertThat(provider.getChildren(null), equalTo(emptyArray()));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayParentHasNoChildren() {
    assertThat(provider.getChildren(newPath()), equalTo(emptyArray()));
  }

  @Test
  public void getChildrenShouldReturnFilteredChildrenWhenFilterIsUsed() {
    Object integer = 1;
    Object string = "2";
    Object[] initialChildren = {integer, string};
    Object[] expectedChildren = {integer};

    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, initialChildren)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getElementsShouldRetainDuplicateElementsWhenFiltering() {
    Object[] initialElements = {0, "abc", 0, "def"};
    Object[] expectedElements = {0, 0};

    Object input = new Object();
    FilterableContentProvider p = new Builder().input(input, initialElements)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void getElementsShouldRetainDuplicateElementsWhenNoFilterIsUsed() {
    Object input = new Object();
    Object[] elements = elements(0, 1, 1, 0);
    FilterableContentProvider p = new Builder().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void getElementsShouldRetainTheOrderOfTheElementsWhenFiltering() {
    Object[] initialChildren = {1, "2", 3};
    Object[] expectedChildren = {1, 3};

    Object input = "";
    FilterableContentProvider p = new Builder().input(input, initialChildren)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getElements(input), equalTo(expectedChildren));
  }

  @Test
  public void getElementsShouldRetainTheOrderOfTheElementsWhenNoFilterIsUsed() {
    Object input = "";
    Object[] elements = elements(1, "2", 3);
    FilterableContentProvider p = new Builder().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void getElementsShouldReturnAllElementsIfThereIsNoFilter() {
    Object input = 0;
    Object[] elements = {1, 2, 3};
    FilterableContentProvider p = new Builder().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfAllElementsAreFilteredOut() {
    Object[] initialElements = {1, 2, 3};
    Object[] expectedElements = {};

    Object input = "";
    FilterableContentProvider p = new Builder().input(input, initialElements)
        .filter(instanceOf(Integer.class)).build();

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfInputHasNoElements() {
    assertThat(provider.getElements(0), equalTo(emptyArray()));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfInputIsNull() {
    assertThat(provider.getElements(null), equalTo(emptyArray()));
  }

  @Test
  public void getElementsShouldReturnFilteredElementsWhenFilterIsUsed() {
    Object[] initialElements = {"1", 2, 3};
    Object[] expectedElements = {2, 3};

    Object input = "";
    FilterableContentProvider p = new Builder().input(input, initialElements)
        .filter(instanceOf(String.class)).build();

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfAllChildrenAreFilteredOut() {
    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, elements(1, "2"))
        .filter(instanceOf(Object.class)).build();

    assertThat(p.hasChildren(newPath()), is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfParentHasNoChildren() {
    assertThat(provider.hasChildren(newPath()), is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfParentIsNull() {
    assertThat(provider.hasChildren(null), is(false));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfNotAllChildrenAreFilteredOut() {
    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, elements(0, "1"))
        .filter(instanceOf(String.class)).build();
    assertThat(p.hasChildren(parent), is(true));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfParentHasChildrenAndThereIsNoFilter() {
    TreePath parent = newPath();
    FilterableContentProvider p = new Builder().map(parent, elements(0)).build();
    assertThat(p.hasChildren(parent), is(true));
  }

  /**
   * @return An array of the given elements.
   */
  private Object[] elements(Object... children) {
    return children;
  }

  /**
   * @return An empty array.
   */
  private Object[] emptyArray() {
    return elements();
  }

  /**
   * @return A {@link TreePath} containing the given segments.
   */
  private TreePath newPath(Object... segments) {
    return new TreePath(segments);
  }
}