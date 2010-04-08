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

import rabbit.ui.internal.pages.DateStyledCellLabelProvider;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @see DateStyledCellLabelProvider
 */
@SuppressWarnings("restriction")
public class DateStyledCellLabelProviderTest {

  protected static final Shell shell;
  protected static final DateStyledCellLabelProvider labels;
  private static final TableViewer viewer;

  static {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    labels = new DateStyledCellLabelProvider();
    viewer = new TableViewer(shell);
    viewer.setLabelProvider(labels);
    viewer.setContentProvider(new IStructuredContentProvider() {

      @Override
      public void dispose() {
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

    });
  }

  @AfterClass
  public static void disposeShell() {
    shell.dispose();
  }

  @Before
  public void removeTableItems() {
    viewer.getTable().removeAll();
  }

  @Test
  public void testUpdate_labelOfLocalDate_today() {
    // A date representing today:
    LocalDate date = new LocalDate();
    viewer.setInput(new Object[] { date });
    // The date has been created as the first item in the tree:
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(date.toString() + " [Today]", item.getText());
  }

  @Test
  public void testUpdate_labelOfLocalDate_yesterday() {
    // A date representing yesterday:
    LocalDate date = new LocalDate().minusDays(1);
    viewer.setInput(new Object[] { date });

    // The date has been created as the first item in the tree:
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(date.toString() + " [Yestoday]", item.getText());
  }

  /**
   * Creates a label provider for testing.
   */
  protected DateStyledCellLabelProvider createLabelProvider() {
    return new DateStyledCellLabelProvider();
  }
}
