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
package rabbit.ui.viewers;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * @see FilterableContentProvider
 */
public class FilterableContentProviderTest {

  /**
   * Helper class for building a {@link FilterableContentProvider} for testing.
   */
  private static class Tester {

    final Map<TreePath, Object[]> data = Maps.newHashMap();
    final Set<Predicate<?>> filters = Sets.newHashSet();
    Object input = new Object();
    Object[] inputElements = new Object[0];

    /** Constructor. */
    Tester() {
    }

    /**
     * Builds a {@link FilterableContentProvider} using the configurations set
     * in this object.
     * 
     * @return A {@link FilterableContentProvider}.
     */
    FilterableContentProvider build() {
      return new FilterableContentProvider(filters.toArray(new Predicate[0])) {

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
          return (children == null) ? new Object[0] : children;
        }

        @Override
        protected Object[] doGetElements(Object theInput) {
          return (Objects.equal(input, theInput)) ? inputElements
              : new Object[0];
        }
      };
    }

    /**
     * Adds the given filter to the collection of filters for building the
     * content provider.
     * 
     * @param filter
     *          The filter to add.
     * @return {@code this}
     */
    Tester filter(Predicate<?> filter) {
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
    Tester input(Object input, Object[] elements) {
      this.input = input;
      this.inputElements = elements;
      return this;
    }

    /**
     * Maps the given parent to the given children. When
     * {@code getChildren(parent} is called on the content provider built by
     * this object, {@code children} will be returned.
     * 
     * @param parent
     *          The parent path.
     * @param children
     *          The children of the parent path.
     * @return {@code this}
     */
    Tester map(TreePath parent, Object[] children) {
      data.put(parent, children);
      return this;
    }
  }

  protected FilterableContentProvider provider;

  @Before
  public void before() {
    provider = new Tester().build();
  }

  @Test
  public void testGetChildren_argIsNull() {
    // No exceptions
    assertThat(provider.getChildren(null), equalTo(emptyArray()));
  }

  @Test
  public void testGetChildren_retainsDuplicateChildrenFiltered() {
    // Test that duplicate children is not removed by filtering:
    TreePath parent = parent();
    Object[] children = {0, 0, "a"}; // Children with duplicate elements
    FilterableContentProvider p = new Tester().map(parent, children)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getChildren(parent), equalTo(elements(0, 0)));
  }

  @Test
  public void testGetChildren_retainsDuplicateChildrenUnfiltered() {
    // Test that duplicate children is not removed:
    TreePath parent = parent();
    Object[] children = {0, 0, 0}; // Children with duplicate elements
    FilterableContentProvider p = new Tester().map(parent, children).build();
    assertThat(p.getChildren(parent), equalTo(children));
  }

  @Test
  public void testGetChildren_retainsOriginalOrderFiltered() {
    // Test that the order of the children element is not modified by
    // filtering
    TreePath parent = parent();
    Object[] children = {3, "2", 1}; // "2" will be removed by filtering
    FilterableContentProvider p = new Tester().map(parent, children)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getChildren(parent), equalTo(elements(3, 1)));
  }

  @Test
  public void testGetChildren_retainsOriginalOrderUnfiltered() {
    // Test that the order of the children element is not modified
    TreePath parent = parent();
    Object[] children = {9, 29, "adf", 0};
    FilterableContentProvider p = new Tester().map(parent, children).build();
    assertThat(p.getChildren(parent), equalTo(children));
  }

  @Test
  public void testGetChildren_returnsEmptyFilteredChildren() {
    TreePath parent = parent();
    Object[] children = {1, "2", 3l};
    FilterableContentProvider p = new Tester().map(parent, children)
        .filter(instanceOf(Object.class)).build();
    assertThat(p.getChildren(parent), equalTo(emptyArray()));
  }

  @Test
  public void testGetChildren_returnsFilteredChildren() {
    TreePath parent = parent();
    Object integer = 1;
    Object string = "2";
    Object[] children = {integer, string};
    FilterableContentProvider p = new Tester().map(parent, children)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getChildren(parent), equalTo(elements(integer)));
  }

  @Test
  public void testGetChildren_returnsUnfilteredChildren() {
    TreePath p1 = parent(10);
    TreePath p2 = parent("");
    Object[] c1 = {158, 290};
    Object[] c2 = {"1", "2"};
    FilterableContentProvider f = new Tester().map(p1, c1).map(p2, c2).build();
    assertThat(f.getChildren(p1), equalTo(c1));
    assertThat(f.getChildren(p2), equalTo(c2));
  }

  @Test
  public void testGetChildren_returnsUnfilteredEmptyArray() {
    assertThat(provider.getChildren(parent()), equalTo(emptyArray()));
  }

  @Test
  public void testGetElements_argIsNll() {
    assertThat(provider.getElements(null), equalTo(emptyArray()));
  }

  @Test
  public void testGetElements_retainsDuplicateElementsFiltered() {
    Object input = new Object();
    Object[] elements = elements(0, "abc", 0, "def");
    FilterableContentProvider p = new Tester().input(input, elements)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getElements(input), equalTo(elements(0, 0)));
  }

  @Test
  public void testGetElements_retainsDuplicateElementsUnfiltered() {
    Object input = new Object();
    Object[] elements = elements(0, 1, 1, 0);
    FilterableContentProvider p = new Tester().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void testGetElements_retainsOriginalOrderFiltered() {
    Object input = "";
    Object[] elements = elements(1, "2", 3);
    FilterableContentProvider p = new Tester().input(input, elements)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getElements(input), equalTo(elements(1, 3)));
  }

  @Test
  public void testGetElements_retainsOriginalOrderUnfilterd() {
    Object input = "";
    Object[] elements = elements(1, "2", 3);
    FilterableContentProvider p = new Tester().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void testGetElements_returnsFilteredElements() {
    Object input = "";
    Object[] elements = elements("1", 2, 3);
    FilterableContentProvider p = new Tester().input(input, elements)
        .filter(instanceOf(String.class)).build();
    assertThat(p.getElements(input), equalTo(elements(2, 3)));
  }

  @Test
  public void testGetElements_returnsFilteredEmptyArray() {
    Object input = "";
    Object[] children = {1, 2, 3};
    FilterableContentProvider p = new Tester().input(input, children)
        .filter(instanceOf(Integer.class)).build();
    assertThat(p.getElements(input), equalTo(emptyArray()));
  }

  @Test
  public void testGetElements_returnsUnfileredEmptyArray() {
    assertThat(provider.getElements(0), equalTo(emptyArray()));
  }

  @Test
  public void testGetElements_returnsUnfiltedElements() {
    Object input = 0;
    Object[] elements = {1, 2, 3};
    FilterableContentProvider p = new Tester().input(input, elements).build();
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void testGetFilters_empty() {
    assertThat(provider.getFilters(), notNullValue());
    assertThat(provider.getFilters().isEmpty(), is(true));
  }

  @Test
  public void testGetFilters_notEmpty() {
    FilterableContentProvider p = new Tester().filter(instanceOf(String.class))
        .filter(instanceOf(Integer.class)).build();
    assertThat(p.getFilters(), notNullValue());
    assertThat(p.getFilters().size(), is(2));
    assertThat(p.getFilters().contains(instanceOf(Integer.class)), is(true));
    assertThat(p.getFilters().contains(instanceOf(String.class)), is(true));
  }

  @Test
  public void testHasChildren_argIsNull() {
    assertThat(provider.hasChildren(null), is(false));
  }

  @Test
  public void testHasChildren_returnsFalseFiltered() {
    assertThat(provider.hasChildren(parent()), is(false));
  }

  @Test
  public void testHasChildren_returnsFalseUnfiltered() {
    assertThat(provider.hasChildren(parent()), is(false));
  }

  @Test
  public void testHasChildren_returnsTrueFiltered() {
    TreePath parent = parent();
    FilterableContentProvider p = new Tester().map(parent, elements(0, "1"))
        .filter(instanceOf(String.class)).build();
    assertThat(p.hasChildren(parent), is(true));
  }

  @Test
  public void testHasChildren_returnsTrueUnfiltered() {
    TreePath parent = parent();
    FilterableContentProvider p = new Tester().map(parent, elements(0)).build();
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
  private TreePath parent(Object... segments) {
    return new TreePath(segments);
  }
}
