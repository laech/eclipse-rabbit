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

import static rabbit.ui.MillisConverter.toDefaultString;

import rabbit.ui.CellPainter.IValueProvider;
import rabbit.ui.internal.pages.ValueColumnLabelProvider;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @see ValueColumnLabelProvider
 */
@SuppressWarnings("restriction")
public class ValueColumnLabelProviderTest {

  private static final Shell shell;
  private static final TreeViewer viewer;
  private static final IValueProvider provider;
  private static final ValueColumnLabelProvider labels;

  static {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    viewer = new TreeViewer(shell);
    viewer.setContentProvider(new ITreeContentProvider() {

      @Override
      public Object[] getChildren(Object parentElement) {
        return new Object[0];
      }

      @Override
      public Object getParent(Object element) {
        return null;
      }

      @Override
      public boolean hasChildren(Object element) {
        return true;
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
      }

      @Override
      public void dispose() {
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

    });
    provider = new IValueProvider() {

      @Override
      public int getColumnWidth() {
        return 0;
      }

      @Override
      public long getMaxValue() {
        return 0;
      }

      @Override
      public long getValue(Object element) {
        if (element instanceof String)
          return 120;
        else
          return 10;
      }

      @Override
      public boolean shouldPaint(Object element) {
        return element instanceof String || element instanceof Integer;
      }
    };
    labels = new ValueColumnLabelProvider(provider);
    viewer.setLabelProvider(labels);
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Before
  public void before() {
    viewer.getTree().removeAll();
  }

  @Test
  public void testUpdate_shouldPaintTrue() {
    // We have already specified our value provider to return false for String
    // and Integer types, (IValueProvider.shoudlPaint(Object))

    String str = "287snls";
    viewer.setInput(new String[] { str });
    TreeItem item = viewer.getTree().getItem(0);
    assertEquals(toDefaultString(provider.getValue(str)), item.getText());

    int i = 128734;
    viewer.setInput(new Integer[] { i });
    item = viewer.getTree().getItem(0);
    assertEquals(toDefaultString(provider.getValue(i)), item.getText());
  }

  @Test
  public void testUpdate_shouldPaintFalse() {
    // We have already specified our value provider to return false for Object
    // type, (IValueProvider.shoudlPaint(Object))

    Object obj = new Object();
    viewer.setInput(new Object[] { obj });
    TreeItem item = viewer.getTree().getItem(0);
    assertEquals("", item.getText());
  }
}
