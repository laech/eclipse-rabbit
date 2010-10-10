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

import rabbit.ui.viewers.FilteredTreeNodeContentProvider;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreeNode;
import org.junit.Before;
import org.junit.Test;

/**
 * @see FilteredTreeNodeContentProvider
 */
public class FilteredTreeNodeContentProviderTest {
  
  private FilteredTreeNodeContentProvider c;
  
  @Before
  public void before() {
    c = create();
  }
  
  @Test
  public void test_filtersSubclasses() {
    FilteredTreeNodeContentProvider provider = create(instanceOf(Object.class));
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(0), new TreeNode("1") });
    try {
      assertThat(provider.getChildren(parent), equalTo(new Object[0]));
      assertThat(provider.hasChildren(parent), is(false));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void test_multipleFilters() {
    FilteredTreeNodeContentProvider provider = create(
        instanceOf(String.class), instanceOf(Integer.class));
    TreeNode parent = new TreeNode(new Object());
    TreeNode string = new TreeNode("");
    TreeNode integer = new TreeNode(0);
    TreeNode bool = new TreeNode(Boolean.TRUE);
    parent.setChildren(new TreeNode[] { string, integer, bool });
    try {
      assertThat(provider.hasChildren(parent), is(true));
      assertThat(provider.getChildren(parent), equalTo(new Object[] { bool }));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testGetChildren_argIsNull() {
    assertThat(c.getChildren(null), equalTo(new Object[0]));
  }
  
  @Test
  public void testGetChildren_argIsNullInTreeNode() {
    TreeNode parent = new TreeNode(null);
    parent.setChildren(new TreeNode[] { new TreeNode("") });
    assertThat(c.getChildren(parent), equalTo((Object[]) parent.getChildren()));
  }
  
  @Test
  public void testGetChildren_returnsEmptyArray() {
    TreeNode parent = new TreeNode("");
    assertThat(c.getChildren(parent), equalTo(new Object[0]));
  }
  
  @Test
  public void testGetChildren_returnsEmptyFilteredArray() {
    FilteredTreeNodeContentProvider provider = create(instanceOf(Integer.class));
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(1), new TreeNode(2) });
    assertThat(provider.getChildren(parent), equalTo(new Object[0]));
  }
  
  @Test
  public void testGetChildren_returnsNotEmptyArray() {
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(1), new TreeNode(2) });
    assertThat(c.getChildren(parent), equalTo((Object[]) parent.getChildren()));
  }
  
  @Test
  public void testGetChildren_returnsNotEmptyFilteredArray() {
    FilteredTreeNodeContentProvider provider = create(instanceOf(Integer.class));
    TreeNode parent = new TreeNode("");
    TreeNode string = new TreeNode("");
    TreeNode integer = new TreeNode(1);
    parent.setChildren(new TreeNode[] { string, integer });
    try {
      assertThat(provider.getChildren(parent), equalTo(new Object[] { string }));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testGetElements_argIsNull() {
    assertThat(c.getElements(null), notNullValue());
    assertThat(c.getElements(null).length, equalTo(0));
  }
  
  @Test
  public void testGetFilters_empty() {
    assertThat(c.getFilters(), notNullValue());
    assertThat(c.getFilters().size(), is(0));
  }
  
  @Test
  public void testGetFilters_notEmpty() {
    Predicate<Object> string = Predicates.instanceOf(String.class);
    Predicate<Object> integer = Predicates.instanceOf(Integer.class);
    FilteredTreeNodeContentProvider provider = create(string, integer);
    try {
      assertThat(provider.getFilters().size(), is(2));
      assertThat(provider.getFilters().contains(string), is(true));
      assertThat(provider.getFilters().contains(integer), is(true));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testGetParent_argIsNull() {
    assertThat(c.getParent(null), nullValue());
  }
  
  @Test
  public void testGetParent_returnsNonnull() {
    TreeNode parent = new TreeNode("");
    TreeNode child = new TreeNode(0);
    child.setParent(parent);
    assertThat(c.getParent(child), is((Object) parent));
  }
  
  @Test
  public void testGetParent_returnsNull() {
    assertThat(c.getParent(""), nullValue());
  }
  
  @Test
  public void testHasChildren_argIsNull() {
    assertThat(c.hasChildren(null), is(false));
  }
  
  @Test
  public void testHashChildren_returnsFalseWhenAllChildrenAreFiltered() {
    FilteredTreeNodeContentProvider provider = create(
        instanceOf(String.class), instanceOf(Integer.class));
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(1), new TreeNode("1") });
    try {
      assertThat(provider.hasChildren(parent), is(false));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testHashChildren_returnsFalseWhenParentHasNoChildren() {
    assertThat(c.hasChildren(new TreeNode("")), is(false));
  }
  
  @Test
  public void testHashChildren_returnsTrueWhenChildrenAreNotFiltered() {
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(0) });
    assertThat(c.hasChildren(parent), is(true));
  }
  
  @Test
  public void testHashChildren_returnsTrueWhenParentHasUnfilteredChildren() {
    FilteredTreeNodeContentProvider provider = create(instanceOf(String.class));
    TreeNode parent = new TreeNode("");
    parent.setChildren(new TreeNode[] { new TreeNode(""), new TreeNode(0) });
    try {
      assertThat(provider.hasChildren(parent), is(true));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testInputChanged_argNewInputIsNull() {
    c.inputChanged(null, "", null); // No exceptions
  }
  
  @Test
  public void testInputChanged_argOldInputIsNull() {
    c.inputChanged(null, null, ""); // No exceptions
  }
  
  @Test
  public void testInputChanged_argsAreNull() {
    c.inputChanged(null, null, null);
  }
  
  @Test
  public void testInputChanged_argViewerIsNull() {
    c.inputChanged(null, "", ""); // No exceptions
  }
  
  protected FilteredTreeNodeContentProvider create(Predicate<?>... filters) {
    return new FilteredTreeNodeContentProvider(filters);
  }
}
