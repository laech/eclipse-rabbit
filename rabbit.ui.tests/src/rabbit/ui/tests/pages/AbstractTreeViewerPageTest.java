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
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.AbstractTreeViewerPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @see AbstractTreeViewerPage
 */
@SuppressWarnings("restriction")
public abstract class AbstractTreeViewerPageTest extends
    AbstractValueProviderPageTest {

  protected AbstractTreeViewerPage page;

  @Before
  public void setUp() {
    page = createPage();
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        page.createContents(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell());
      }
    });
  }

  @Test
  public void testCreateInitialComparator() throws Exception {
    assertNotNull(createInitialComparator(page, page.getViewer()));
  }

  @Test
  public void testCreateContentProvider() throws Exception {
    assertNotNull(createContentProvider(page));
  }

  @Test
  public void testGetViewer() throws Exception {
    assertNotNull(page.getViewer());
  }

  @Test
  public void testSaveState() throws Exception {
    int width = 12;
    for (TreeColumn column : page.getViewer().getTree().getColumns()) {
      column.setWidth(width);
    }
    saveState(page);

    for (TreeColumn column : page.getViewer().getTree().getColumns()) {
      column.setWidth(width * 2);
    }

    restoreState(page);
    for (TreeColumn column : page.getViewer().getTree().getColumns()) {
      assertEquals(width, column.getWidth());
    }
  }

  protected ViewerComparator createInitialComparator(AbstractTreeViewerPage page,
      TreeViewer viewer) throws Exception {
    Method createComparator = AbstractTreeViewerPage.class.getDeclaredMethod(
        "createInitialComparator", TreeViewer.class);
    createComparator.setAccessible(true);
    return (ViewerComparator) createComparator.invoke(page, viewer);
  }

  protected IContentProvider createContentProvider(AbstractTreeViewerPage page)
      throws Exception {
    Method createContentProvider = AbstractTreeViewerPage.class
        .getDeclaredMethod("createContentProvider");
    createContentProvider.setAccessible(true);
    return (IContentProvider) createContentProvider.invoke(page);
  }

  @Override
  protected abstract AbstractTreeViewerPage createPage();

  protected void restoreState(AbstractTreeViewerPage page) throws Exception {
    Method restoreState = AbstractTreeViewerPage.class
        .getDeclaredMethod("restoreState");
    restoreState.setAccessible(true);
    restoreState.invoke(page);
  }

  protected void saveState(AbstractTreeViewerPage page) throws Exception {
    Method saveState = AbstractTreeViewerPage.class
        .getDeclaredMethod("saveState");
    saveState.setAccessible(true);
    saveState.invoke(page);
  }
}
