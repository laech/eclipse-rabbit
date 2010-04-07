/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.AbstractDateCategoryContentProvider;
import rabbit.ui.internal.pages.AbstractTreeViewerPage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @see AbstractDateCategoryContentProvider
 */
@SuppressWarnings("restriction")
public abstract class AbstractDateCategoryContentProviderTest {

  /**
   * Content provider created by
   * {@link #createContentProvider(AbstractTreeViewerPage, boolean)}.
   */
  protected final AbstractDateCategoryContentProvider contentProvider;
  /** Page created by {@link #createPage()}. */
  protected final AbstractTreeViewerPage page;
  private static final Shell shell;

  static {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  public AbstractDateCategoryContentProviderTest() {
    page = createPage();
    page.createContents(shell);

    contentProvider = createContentProvider(page, false);
    page.getViewer().setContentProvider(contentProvider);
  }

  /**
   * Creates a page to help testing.
   */
  protected abstract AbstractTreeViewerPage createPage();

  /**
   * Creates a content provider for testing.
   */
  protected abstract AbstractDateCategoryContentProvider createContentProvider(
      AbstractTreeViewerPage page, boolean displayByDate);

  @Test
  public void testSetDisplayByDate() {
    contentProvider.setDisplayByDate(false);
    assertFalse(contentProvider.isDisplayingByDate());
    contentProvider.setDisplayByDate(true);
    assertTrue(contentProvider.isDisplayingByDate());
  }
}
