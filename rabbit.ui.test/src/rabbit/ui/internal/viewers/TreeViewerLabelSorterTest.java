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

import rabbit.ui.internal.pages.CollectionContentProvider;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerSorter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see TreeViewerLabelSorter
 */
public final class TreeViewerLabelSorterTest extends TreeViewerSorterTest {

  private static class StringArrayLabelProvider extends BaseLabelProvider 
      implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      return ((String[]) element)[columnIndex];
    }
  }
  
  private static class StringLabelProvider extends BaseLabelProvider
      implements ILabelProvider {

    @Override
    public Image getImage(Object element) {
      return null;
    }

    @Override
    public String getText(Object element) {
      return (String) element;
    }
    
  }
  
  /**
   * Test when a column is clicked, the items in the viewer are sorted.
   */
  @Test
  public void testSelectColumn_itemsSorted() throws Exception {
    TreeViewer viewer = new TreeViewer(shell);
    viewer.setContentProvider(new CollectionContentProvider());
    viewer.setLabelProvider(new StringLabelProvider());
    
    TreeViewerSorter sorter = createViewerSorter(viewer);
    assertNull(sorter.getSelectedColumn());

    Tree tree = sorter.getViewer().getTree();
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    
    String bigger = "b";
    String smaller = "a";
    viewer.setInput(Arrays.asList(bigger, smaller));
    
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.UP, tree.getSortDirection());
    assertEquals(smaller, tree.getItem(0).getData());
    assertEquals(bigger, tree.getItem(1).getData());
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.DOWN, tree.getSortDirection());
    assertEquals(bigger, tree.getItem(0).getData());
    assertEquals(smaller, tree.getItem(1).getData());
  }
  
  /**
   * Test when a column is clicked, the items in the viewer are sorted, if an
   * item's label is null, it's considered the smallest value.
   */
  @Test
  public void testSelectColumn_itemsSorted_withNullLabel() throws Exception {
    TreeViewer viewer = new TreeViewer(shell);
    viewer.setContentProvider(new CollectionContentProvider());
    
    TreeViewerSorter sorter = createViewerSorter(viewer);
    assertNull(sorter.getSelectedColumn());

    Tree tree = sorter.getViewer().getTree();
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    
    final String bigger = "b";
    final String smaller = "";
    viewer.setLabelProvider(new StringLabelProvider() {
      @Override
      public String getText(Object element) {
        if (element == smaller) {
          return null;
        }
        return super.getText(element);
      }
    });
    viewer.setInput(Arrays.asList(bigger, smaller));
    
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.UP, tree.getSortDirection());
    assertEquals(smaller, tree.getItem(0).getData());
    assertEquals(bigger, tree.getItem(1).getData());
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.DOWN, tree.getSortDirection());
    assertEquals(bigger, tree.getItem(0).getData());
    assertEquals(smaller, tree.getItem(1).getData());
  }

  /**
   * Test when a tree has multiple columns, and a column other than the first
   * column is clicked, the items in the viewer are sorted.
   */
  @Test
  public void testSelectColumn_itemsSorted_multipleColumns() throws Exception {
    TreeViewer viewer = new TreeViewer(shell);
    viewer.setContentProvider(new CollectionContentProvider());
    viewer.setLabelProvider(new StringArrayLabelProvider());
    
    TreeViewerSorter sorter = createViewerSorter(viewer);
    assertNull(sorter.getSelectedColumn());

    Tree tree = sorter.getViewer().getTree();
    new TreeColumn(tree, SWT.NONE); // First column
    TreeColumn column2 = new TreeColumn(tree, SWT.NONE); // Second column
    
    String[] bigger = { "", "2" };
    String[] smaller = { "", "1" };
    viewer.setInput(Arrays.asList(bigger, smaller));
    
    Event event = new Event();
    event.widget = column2;
    SelectionEvent selectionEvent = new SelectionEvent(event);
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column2, tree.getSortColumn());
    assertEquals(SWT.UP, tree.getSortDirection());
    assertEquals(smaller, tree.getItem(0).getData());
    assertEquals(bigger, tree.getItem(1).getData());
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column2, tree.getSortColumn());
    assertEquals(SWT.DOWN, tree.getSortDirection());
    assertEquals(bigger, tree.getItem(0).getData());
    assertEquals(smaller, tree.getItem(1).getData());
  }

  @Override
  protected TreeViewerSorter createViewerSorter(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer);
  }
}
