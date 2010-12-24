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

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.FILE;

import rabbit.ui.IProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider2;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.TreePathValueProvider;

import com.google.common.collect.Lists;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.util.Arrays.asList;

import java.util.List;

/**
 * Tests for {@link StateHelper}.
 */
public class StateHelperTest {

  private StateHelper persistence;
  private Shell shell;
  private Tree tree;

  @Before
  public void create() throws Exception {
    persistence = StateHelper.of(XMLMemento.createWriteRoot("Testing"), "MyId");
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    tree = new Tree(shell, SWT.NONE);
  }

  @After
  public void dispose() {
    shell.dispose();
  }

  @Test
  public void restoreCategoriesShouldDoNothingIfNoCategoriesHaveBeenSaved() {
    ICategoryProvider2 provider = mock(ICategoryProvider2.class);
    doThrow(new AssertionError()).when(provider).setSelected(
        Mockito.<ICategory[]> any());
    persistence.restoreCategories(provider);
  }

  @Test(expected = NullPointerException.class)
  public void restoreCategoriesShouldThrowAnExceptionIfProviderIsNull() {
    persistence.restoreCategories(null);
  }

  @Test
  public void restoreColumnWidthsShouldDoNothingIfNoWidthsHaveBeenSaved() {
    int width = 123;
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    column.setWidth(width);

    persistence.restoreColumnWidths(new TreeColumn[]{column});
    assertThat(column.getWidth(), equalTo(width));
  }

  @Test(expected = NullPointerException.class)
  public void restoreColumnWidthsShouldThrowAnExceptionIfArgumentIsNull() {
    persistence.restoreColumnWidths(null);
  }

  @Test(expected = NullPointerException.class)
  public void restoreColumnWidthsShouldThrowAnExceptionIfColumnsContainNull() {
    persistence.restoreColumnWidths(new TreeColumn[]{null});
  }

  @Test
  public void restoreVisualCategoryShouldDoNothingIsNoCategoryHasBeenSaved() {
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider provider = new TreePathValueProvider(categorizer,
        mock(IProvider.class), mock(IConverter.class));

    provider.setVisualCategory(FILE);
    persistence.restoreVisualCategory(provider);

    assertThat(provider.getVisualCategory(), equalTo((ICategory) FILE));
  }

  @Test(expected = NullPointerException.class)
  public void restoreVisualCategoryShouldThrowAnExceptionIfArgumentIsNull() {
    persistence.restoreVisualCategory(null);
  }

  @Test(expected = NullPointerException.class)
  public void saveCategoriesShouldThrowAnExceptionIfCategoriesContainNull() {
    persistence.saveCategories(new Category[]{null});
  }

  @Test(expected = NullPointerException.class)
  public void saveColumnWidthsShouldThrowAnExceptionIfArgumentIsNull() {
    persistence.saveColumnWidths(null);
  }

  @Test(expected = NullPointerException.class)
  public void saveColumnWidthsShouldThrowAnExceptionIfColumnsContainNull() {
    persistence.saveColumnWidths(new TreeColumn[]{null});
  }

  @Test(expected = NullPointerException.class)
  public void saveVisualCategoryShouldThrowAnExceptionIfArgumentIsNull() {
    persistence.saveVisualCategory(null);
  }

  @Test
  public void shouldBeAbleToPersistAndRestoreMultipleStates() {
    // For saving the visual category:
    Category visualCategory = FILE;
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider valueProvider = new TreePathValueProvider(
        categorizer, mock(IProvider.class), mock(IConverter.class),
        visualCategory);

    // For saving the column widths:
    int width = 101;
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    column.setWidth(width);

    // For saving the selected categories:
    final List<Category> expectedCategories = asList(DATE, FILE);
    final List<Category> actualCategories = Lists.newArrayList();
    ICategoryProvider2 categoryProvider = mock(ICategoryProvider2.class);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        for (Object obj : invocation.getArguments()) {
          actualCategories.add((Category) obj);
        }
        return null;
      }
    }).when(categoryProvider)
        .setSelected(expectedCategories.toArray(new Category[0]));

    // Save the states:
    persistence
        .saveCategories(expectedCategories.toArray(new Category[0]))
        .saveVisualCategory(visualCategory)
        .saveColumnWidths(new TreeColumn[]{column});

    // Change the states:
    categoryProvider.setSelected(FILE);
    valueProvider.setVisualCategory(DATE);
    column.setWidth(1);

    // Restore:
    persistence
        .restoreCategories(categoryProvider)
        .restoreVisualCategory(valueProvider)
        .restoreColumnWidths(new TreeColumn[]{column});

    assertThat(actualCategories, equalTo(expectedCategories));
    assertThat(column.getWidth(), equalTo(width));
    assertThat(valueProvider.getVisualCategory(),
        equalTo((ICategory) visualCategory));
  }

  @Test
  public void shouldPersistTheCategories() {
    final List<Category> expected = asList(DATE, FILE);
    final List<Category> actual = Lists.newArrayList();

    ICategoryProvider2 provider = mock(ICategoryProvider2.class);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        for (Object obj : invocation.getArguments()) {
          actual.add((Category) obj);
        }
        return null;
      }
    }).when(provider).setSelected(expected.toArray(new Category[0]));

    persistence.saveCategories(expected.toArray(new Category[0]));
    persistence.restoreCategories(provider);

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void shouldPersistTheColumnWidths() {
    int width1 = 101;
    int width2 = 20;

    TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
    column1.setWidth(width1);
    TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
    column2.setWidth(width2);

    persistence.saveColumnWidths(new TreeColumn[]{column1, column2});
    column1.setWidth(1);
    column2.setWidth(2);
    persistence.restoreColumnWidths(new TreeColumn[]{column1, column2});

    assertThat(column1.getWidth(), equalTo(width1));
    assertThat(column2.getWidth(), equalTo(width2));
  }

  @Test
  public void shouldPersistTheVisualCategory() {
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider provider = new TreePathValueProvider(categorizer,
        mock(IProvider.class), mock(IConverter.class), FILE);

    persistence.saveVisualCategory(DATE);
    persistence.restoreVisualCategory(provider);

    assertThat(provider.getVisualCategory(), equalTo((ICategory) DATE));
  }
}
