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

import rabbit.ui.internal.viewers.ITreePathBuilder;
import rabbit.ui.internal.viewers.TreePathContentProvider;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Tests for a {@link TreePathContentProvider}.
 */
public class TreePathContentProviderTest {

  private static final Object[] EMPTY_ARRAY = {};

  private TreePathContentProvider content;

  @Before
  public void setup() {
    Collection<TreePath> noData = Collections.emptyList();
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    when(builder.build(Mockito.any())).thenReturn(noData);
    content = create(builder);
  }

  @Test
  public void aChildHasNoPossibleParentShouldGetNoParents() {
    // Given a non-null child that has no possible parents:
    Object child = this;

    // When asking for the possible parents of the child:
    TreePath[] parents = content.getParents(child);

    // Then an empty array is returned as the parents:
    assertThat(parents, is(EMPTY_ARRAY));
  }

  @Test
  public void aChildHasPossibleParentsShouldGetTheParents() {
    // Given we have two leaves that have the same last segments:
    Object child = "Child";
    TreePath leaf1 = new TreePath(new Object[]{0, child});
    TreePath leaf2 = new TreePath(new Object[]{2, child});

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build("input")).willReturn(asList(leaf1, leaf2));
    content = create(builder);

    // When getting the parents of the child:
    content.inputChanged(null, null, "input");
    Set<TreePath> actual = newHashSet(content.getParents(child));

    // Then the parent path of the two leaves should be returned:e
    Set<TreePath> expected = newHashSet(
        leaf1.getParentPath(),
        leaf2.getParentPath());
    assertThat(actual, is(expected));
  }

  @Test
  public void aChildThatIsNullShouldGetNoParents() {
    // Given a child is null:
    Object child = null;

    // When asking for possible parents of the child:
    TreePath[] parents = content.getParents(child);

    // Then an empty array is returned:
    assertThat(parents, is(EMPTY_ARRAY));
  }

  @Test
  public void anInputHasNoElementsShouldGetNoElements() {
    // Given an non-null input element that has no child elements:
    Object input = this;

    // When asking for the child elements of the input:
    Object[] elements = content.getElements(input);

    // Then an empty array is returned:
    assertThat(elements, is(EMPTY_ARRAY));
  }

  @Test
  public void anInputThatIsNullShouldGetNoElements() {
    // Given an input is null:
    Object input = null;

    // When asking for child elements of the input:
    Object[] elements = content.getElements(input);

    // Then an empty array should be returned:
    assertThat(elements, is(EMPTY_ARRAY));
  }

  @Test
  public void aParentHasChildrenShouldGetTheDistinctChildren() {
    // Given a tree path has 3 children, 2 of which are equal:
    TreePath branch = new TreePath(new Object[]{0, 1});
    TreePath leaf1 = branch.createChildPath(2);
    TreePath leaf2 = branch.createChildPath(3);
    TreePath leaf3 = branch.createChildPath(3);
    Object input = new Object();

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(asList(leaf1, leaf2, leaf3));
    content = create(builder);

    // When asking for the children of the parent path:
    content.inputChanged(null, null, input); // Sets the input.
    List<Object> children = newArrayList(content.getChildren(branch));

    // Then the distinct children should be returned:
    List<Object> expected = newArrayList(newHashSet( // Get unique children
        leaf1.getLastSegment(),
        leaf2.getLastSegment(),
        leaf3.getLastSegment()));
    assertThat(children, is(equalTo(expected)));
  }

  @Test
  public void aParentHasNoChildrenShouldGetNoChildren() {
    // Given a non-null parent has no children:
    TreePath parent = TreePath.EMPTY;

    // When asking for the children of the parent:
    Object[] children = content.getChildren(parent);

    // The an empty array is returned:
    assertThat(children, is(EMPTY_ARRAY));
  }

  @Test
  public void aParentThatIsNullShouldGetNoChildren() {
    // Given a parent path is null:
    TreePath parent = null;

    // When asking for the children of the null object:
    Object[] children = content.getChildren(parent);

    // The an empty array should be returned as the children:
    assertThat(children, is(EMPTY_ARRAY));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfTheGivenPathHasNoChildren() {
    // Given a non-null parent has no children:
    TreePath parent = TreePath.EMPTY;

    // When asking whether the parent has children or not:
    boolean hasChildren = content.hasChildren(parent);

    // Then false is returned:
    assertThat(hasChildren, is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfTheGivenPathIsNull() {
    // Given a parent is null:
    TreePath parent = null;

    // When asking whether the parent has children:
    boolean hasChildren = content.hasChildren(parent);

    // Then false is returned:
    assertThat(hasChildren, is(not(true)));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfTheGivenPathHasChildren() {
    // Given a parent has children:
    TreePath parent = new TreePath(new Object[]{0, 1, 2});
    TreePath leaf = parent.createChildPath(100);

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(any())).willReturn(Arrays.asList(leaf));

    content = create(builder);

    // When asking whether the parent has children:
    content.inputChanged(null, null, "");
    boolean hasChildren = content.hasChildren(parent);

    // Then true is returned:
    assertThat(hasChildren, is(true));
  }

  @Test
  public void nullableNewInputShouldBeAcceptable() {
    content.inputChanged(mock(Viewer.class), "", null); // No exception
  }

  @Test
  public void nullableOldInputShouldBeAcceptable() {
    content.inputChanged(mock(Viewer.class), null, ""); // No exception
  }

  @Test
  public void nullableViewerShouldBeAcceptable() {
    content.inputChanged(null, "", ""); // No exception
  }

  @Test
  public void shouldAlwaysReturnTheDataOfTheLatestInput() {
    // Given that an input has already been set:
    List<TreePath> input1 = asList(new TreePath(new Object[]{"a", "b"}));
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input1)).willReturn(input1);
    content = create(builder);
    content.inputChanged(null, null, input1);

    // When new input is set, gets the new root elements:
    TreePath input2 = new TreePath(new Object[]{0, 1});
    given(builder.build(input2)).willReturn(asList(input2));
    content.inputChanged(null, input1, input2);
    Object[] elements = content.getElements(input2);

    // Then data from new input should be returned:
    Object[] expected = new Object[]{input2.getFirstSegment()};
    assertThat(elements, is(expected));
  }

  @Test
  public void shouldBuildTheTreePathsOnInputChange() {
    // Given a content provider is built with a tree path builder:
    Object input = new Object();
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    content = create(builder);

    // When input is set:
    content.inputChanged(null, null, input);

    // Then the content provider should have called the tree path builder:
    verify(builder).build(input);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithoutATreePathBulder() {
    content = create(null);
  }

  @Test
  public void thereShouldBeNoRootElementsIfAllLeavesAreEmptyPaths() {
    // Given all tree paths are empty paths:
    Object input = new Object();
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(
        asList(TreePath.EMPTY, new TreePath(new Object[0])));
    content = create(builder);

    // When set and ask for elements:
    content.inputChanged(null, null, input);
    Object[] elements = content.getElements(input);

    // Then an empty array is returned:
    assertThat(elements, is(EMPTY_ARRAY));
  }

  @Test
  public void theRootElementsShouldBeTheDistinctRootSegmentsOfAllLeaves() {
    // Given we have 3 leaves, the 0th segments of 2 of them are equal::
    TreePath leaf1 = new TreePath(new Object[]{0, 1});
    TreePath leaf2 = new TreePath(new Object[]{2, 3});
    TreePath leaf3 = new TreePath(new Object[]{9, 8});
    Object input = new Object();

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(asList(leaf1, leaf2, leaf3));
    content = create(builder);

    // When asking for elements of the new input:
    content.inputChanged(null, null, input);
    List<Object> actual = newArrayList(content.getElements(input));

    // Then the distinct first elements of each leave is returned:
    List<Object> expected = newArrayList(newHashSet( // Get distinct elements
        leaf1.getFirstSegment(),
        leaf2.getFirstSegment(),
        leaf3.getFirstSegment()));
    assertThat(actual, is(equalTo(expected)));
  }

  /**
   * Creates a content provider for testing.
   */
  private TreePathContentProvider create(ITreePathBuilder builder) {
    return new TreePathContentProvider(builder);
  }

}
