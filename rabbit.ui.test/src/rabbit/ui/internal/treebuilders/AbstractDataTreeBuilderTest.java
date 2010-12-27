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
package rabbit.ui.internal.treebuilders;

import rabbit.data.access.model.IData;
import rabbit.data.access.model.IKey;
import rabbit.ui.IProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider2;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import com.google.common.base.Joiner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.TreePath;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * An abstract test case defined common tests for the internal tree builders.
 * These tree builders all take an {@link ICategoryProvider2} as constructor
 * argument, and all accept input as some kind of {@link IProvider}.
 * 
 * @param <T> the element type to be returned by {@link IProvider#get()}.
 * @see AbstractDataTreeBuilder
 */
public abstract class AbstractDataTreeBuilderTest<T> {

  private ITreePathBuilder builder;
  private T data;
  private IProvider<T> input;

  @Before
  @SuppressWarnings("unchecked")
  public void before() {

    assertThat(dataNode1(), is(not(dataNode2())));
    assertThat(input(null).get(), is(nullValue()));
    assertThat(categories(), is(not(reverse(categories()))));

    data = dataNode1();
    input = input(asList(data));

    Collection<ICategory> noCategories = emptySet();
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getAllSupported()).willReturn(noCategories);
    builder = create(p);
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfTheCategoryProviderIsNull() {
    // Testing the abstract class only:
    Map<ICategory, IKey<Object>> keys = emptyMap();
    new AbstractDataTreeBuilder<IData>(null, keys) {
      @Override
      protected Collection<IData> getData(Object input) {
        return emptySet();
      }
    };
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfTheKeyMapIsNull() {
    // Testing the abstract class only:
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getAllSupported()).willReturn(Collections.<ICategory> emptySet());
    given(p.getSelected()).willReturn(Collections.<ICategory> emptyList());
    given(p.getUnselected()).willReturn(Collections.<ICategory> emptySet());
    new AbstractDataTreeBuilder<IData>(p, null) {
      @Override
      protected Collection<IData> getData(Object input) {
        return emptySet();
      }
    };
  }

  @Test
  public void shouldBuildPathsUsingTheLatestOrderOfTheCategories() {
    ICategory[] categories1 = categories();
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getSelected()).willReturn(asList(categories1));

    // Build something using the previous categories:
    builder = create(p);
    builder.build(input);

    // Reorder the categories and build something else:
    ICategory[] categories2 = reverse(categories1);
    given(p.getSelected()).willReturn(asList(categories2));
    List<TreePath> pathsBuilt = builder.build(input);

    // Then the tree paths are built using the latest order of the categories:
    List<TreePath> expected = buildPaths(data, categories2);
    assertThat(toString(pathsBuilt, expected), pathsBuilt, is(expected));
  }

  @Test
  public void shouldCorrectlyBuildASinglePath() {
    // Given the categories are ordered in a certain order:
    ICategory[] categories = categories();
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getSelected()).willReturn(asList(categories));
    builder = create(p);

    // When a tree path is built:
    List<TreePath> paths = builder.build(input);

    // Then the segments in the tree path should follow that order:
    List<TreePath> expected = buildPaths(data, categories);
    assertThat(toString(paths, expected), paths, is(equalTo(expected)));
  }

  @Test
  public void shouldCorrectlyBuildMultiplePaths() {
    // Given there is more than one data to be built to tree paths:
    ICategory[] categories = categories();
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getSelected()).willReturn(asList(categories));

    List<T> mockData = newArrayList();
    List<TreePath> expected = newArrayList();
    for (int i = 0; i < 2; ++i) {

      // Setup the mock:
      T d = (i % 2 == 0) ? dataNode1() : dataNode2();
      mockData.add(d);

      // Builds the expected path using the order of the categories defined by
      // the category provider:
      expected.addAll(buildPaths(d, categories));
    }

    IProvider<T> input = input(mockData);
    builder = create(p);

    // When the tree paths are built:
    List<TreePath> pathsBuilt = builder.build(input);

    // Then each data node should have its corresponding tree path:
    assertThat(toString(pathsBuilt, expected), pathsBuilt, is(expected));
  }

  @Test
  public void shouldIgnoreTheInputIfTheInputReturnsANullCollection() {
    // Given an input that will return null:
    IProvider<T> dataProvider = input(null);

    // When asking to build from such an input:
    List<TreePath> pathsBuilt = builder.build(dataProvider);

    // Then an empty collection is returned without failing:
    assertThat(pathsBuilt, is(emptyList()));
  }

  @Test
  public void shouldRetainIdenticalPaths() {
    // Given there are some data nodes that contains the same information
    ICategory[] categories = categories();
    ICategoryProvider2 p = mock(ICategoryProvider2.class);
    given(p.getSelected()).willReturn(asList(categories)); // Set up the
                                                           // category order

    List<T> mockData = newArrayList();
    List<TreePath> expected = newArrayList();
    T data = dataNode1();
    List<TreePath> paths = buildPaths(data, categories);
    // Create multiple equal elements:
    for (int i = 0; i < 2; ++i) {
      mockData.add(data);
      expected.addAll(paths);
    }

    IProvider<T> input = input(mockData);
    builder = create(p);

    // When the tree paths are built, all identical paths should be retained:
    List<TreePath> pathsBuilt = builder.build(input);

    // Then each data node should have its corresponding tree path:
    assertThat(toString(pathsBuilt, expected), pathsBuilt, is(expected));
  }
  @Test
  public void shouldReturnAnEmptyCollectionIfInputIsNotRecognized() {
    assertThat(builder.build("NotRecognized"), is(emptyList()));
  }

  @Test
  public void shouldReturnAnEmptyCollectionIsInputIsNull() {
    assertThat(builder.build(null), is(emptyList()));
  }

  /**
   * Builds tree paths according to the given data and categories.
   */
  protected abstract List<TreePath> buildPaths(T data, ICategory... categories);

  /**
   * @return some categories supported by the builder, must be more than one.
   */
  protected abstract ICategory[] categories();

  /**
   * Creates a builder to be tested.
   * @param p the {@link ICategoryProvider2} for the constructor.
   * @return a builder.
   */
  protected abstract ITreePathBuilder create(ICategoryProvider2 p);

  /**
   * Creates a data node that is different to {@link #dataNode2()}.
   */
  protected abstract T dataNode1();

  /**
   * Creates a data node that is different to {@link #dataNode1()}.
   */
  protected abstract T dataNode2();

  /**
   * Creates a provider as an input to the builder.
   * @param inputData the data to be returned by the provider.
   */
  protected abstract IProvider<T> input(@Nullable Collection<T> inputData);

  /**
   * @return an error message to use with assert methods.
   */
  protected String toString(List<TreePath> actual, List<TreePath> expected) {
    List<String> actualString = newArrayList();
    for (TreePath p : actual) {
      actualString.add(toString(p));
    }
    List<String> expectedString = newArrayList();
    for (TreePath p : expected) {
      expectedString.add(toString(p));
    }
    return "\n[" + Joiner.on(", ").join(expectedString) + "] is expected"
        + "\n[" + Joiner.on(", ").join(actualString) + "] is actual";
  }

  /**
   * @return a string representation of the given path.
   */
  protected String toString(TreePath path) {
    List<String> segments = newArrayListWithCapacity(path.getSegmentCount());
    for (int i = 0; i < path.getSegmentCount(); ++i) {
      segments.add(path.getSegment(i).toString());
    }
    return path.getClass().getSimpleName() + "["
        + Joiner.on(", ").join(segments) + "]";
  }

  /**
   * @return an empty list of tree paths.
   */
  private List<TreePath> emptyList() {
    return Collections.emptyList();
  }

  /**
   * Creates a new array that is the inverse of the given array.
   */
  private ICategory[] reverse(ICategory[] elements) {
    List<ICategory> list = newArrayListWithCapacity(elements.length);
    for (ICategory elem : elements) {
      list.add(0, elem);
    }
    return list.toArray(new ICategory[0]);
  }
}
