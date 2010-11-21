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

import rabbit.ui.internal.viewers.TreeViewerSorter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see TreeViewerSorter
 */
public abstract class TreeViewerSorterTest {
  
  /**
   * Shell for creating additional viewers for testing, must not reassign this
   * variable.
   */
  protected static Shell shell;
  
  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }
  
  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_null() throws Exception {
    createViewerSorter(null);
  }
  
  @Test
  public void testGetViewer() throws Exception {
    TreeViewer viewer = new TreeViewer(shell);
    assertSame(viewer, createViewerSorter(viewer).getViewer());
  }
  
  @Test
  public void testGetSelectedColumn() throws Exception {
    TreeViewerSorter sorter = createViewerSorter(new TreeViewer(shell));
    assertNull(sorter.getSelectedColumn());
    
    TreeColumn column = new TreeColumn(sorter.getViewer().getTree(), SWT.NONE);
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);
   
    sorter.widgetSelected(selectionEvent);
    assertSame(column, sorter.getSelectedColumn());
  }
  
  /**
   * Test that when a column is clicked, the sort indicator is updated on the
   * column.
   */
  @Test
  public void testSelectColumn_sortIndication() throws Exception {
    TreeViewerSorter sorter = createViewerSorter(new TreeViewer(shell));
    assertNull(sorter.getSelectedColumn());
    
    TreeColumn column = new TreeColumn(sorter.getViewer().getTree(), SWT.NONE);
    Event event = new Event();
    event.widget = column;
    SelectionEvent selectionEvent = new SelectionEvent(event);
    
    sorter.widgetSelected(selectionEvent);
    Tree tree = sorter.getViewer().getTree();
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.UP, tree.getSortDirection());
    
    sorter.widgetSelected(selectionEvent);
    assertEquals(column, tree.getSortColumn());
    assertEquals(SWT.DOWN, tree.getSortDirection());
  }
  
  /**
   * Creates a viewer sorter for testing. Subclass should create a sorter using
   * the argument directly <strong>without</strong> checking for null.
   * 
   * @param viewer The viewer.
   * @return A viewer sorter for testing.
   */
  protected abstract TreeViewerSorter createViewerSorter(TreeViewer viewer);
}
