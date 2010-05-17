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

import rabbit.ui.IPage;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.TreeViewerSorter;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import java.util.List;

/**
 * Abstract implementation of an {@link IPage} containing a {@link FilteredTree},
 * also defines a column for painting with a {@link CellPainter}.
 */
public abstract class AbstractFilteredTreePage implements IPage {

  private TreeViewerSorter valueSorter;
  private FilteredTree filteredTree;

  /**
   * Constructor.
   */
  public AbstractFilteredTreePage() {
  }
  
  
  @Override
  public void createContents(Composite parent) {
    GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(parent);
    
    int style = SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL;
    filteredTree = new FilteredTree(parent, style, createFilter());
    filteredTree.setBackground(parent.getBackground());
    filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    { // Make it look a bit nicer for us:
      GridLayout layout = (GridLayout) filteredTree.getLayout();
      layout.verticalSpacing = 0;

      layout = (GridLayout) filteredTree.getFilterControl().getParent().getLayout();
      layout.marginHeight = 5;
      layout.marginWidth = 5;
    }

    final TreeViewer viewer = filteredTree.getViewer();
    initializeViewer(viewer);
    
    viewer.setUseHashlookup(true);
    viewer.getTree().setHeaderVisible(true);
    viewer.getTree().addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        saveState();
      }
    });

    // Expand/collapse tree node on double click:
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent e) {
        if (e.getSelection() instanceof ITreeSelection) {
          ITreeSelection selection = (ITreeSelection) e.getSelection();
          
          try {
            TreePath path = selection.getPaths()[0];
            ITreeContentProvider contents = (ITreeContentProvider) viewer.getContentProvider();
            if (contents.hasChildren(selection.getFirstElement()))
              viewer.setExpandedState(path, !viewer.getExpandedState(path));
            
          } catch (NullPointerException ex) {
            ex.printStackTrace();
          } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
          }
        }
      }
    });

    // Hide selection when user clicks on none selectable area:
    viewer.getTree().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        super.mouseDown(e);
        if (viewer.getTree().getItem(new Point(e.x, e.y)) == null)
          viewer.setSelection(StructuredSelection.EMPTY);
      }
    });

    final CellPainter painter = createCellPainter();
    valueSorter = new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        long value1 = painter.getValueProvider().getValue(e1);
        long value2 = painter.getValueProvider().getValue(e2);
        if (value1 == value2)
          return 0;
        else
          return (value1 > value2) ? 1 : -1;
      }
    };

    createColumns(viewer);

    // Special column for painting:
    TreeViewerColumn graphCol = new TreeViewerColumn(viewer, SWT.LEFT);
    graphCol.setLabelProvider(painter);
    graphCol.getColumn().setWidth(100);
    graphCol.getColumn().addSelectionListener(valueSorter);

    for (TreeColumn column : viewer.getTree().getColumns()) {
      column.setMoveable(true);
      column.setResizable(true);
    }
    viewer.setComparator(createInitialComparator(viewer));
    
    restoreState();
  }

  /**
   * Gets the tree of this page.
   * 
   * @return The filtered tree.
   */
  public FilteredTree getFilteredTree() {
    return filteredTree;
  }

  /**
   * Gets the viewer.
   * 
   * @return The viewer.
   */
  public TreeViewer getViewer() {
    return filteredTree.getViewer();
  }

  /**
   * Creates the cell painting for painting the graph column.
   * 
   * @return A cell painter.
   */
  protected abstract CellPainter createCellPainter();

  /**
   * Creates the extra columns for the viewer. The graph column will be created
   * after this.
   * 
   * @param viewer The viewer.
   */
  protected abstract void createColumns(TreeViewer viewer);

  /**
   * Creates a pattern filter for the {@link FilteredTree}.
   * 
   * @return A pattern filter.
   */
  protected abstract PatternFilter createFilter();

  /**
   * Creates a default comparator for sorting the viewer.
   * 
   * @param viewer The viewer.
   * @return A viewer comparator.
   */
  protected abstract ViewerComparator createInitialComparator(TreeViewer viewer);

  /**
   * Gets the sorter for sorting the viewer by value, using the cell painter's
   * {@link IValueProvider}
   * @return The value sorter.
   */
  protected TreeViewerSorter getValueSorter() {
    return valueSorter;
  }

  /**
   * Initialized the viewer, subclasses should use this method set the content
   * provider and label provider for the viewer.
   * @param viewer The viewer to be initialized.
   */
  protected abstract void initializeViewer(TreeViewer viewer);

  /** Restores the state of the page. */
  protected void restoreState() {
    TreeColumn[] columns = getViewer().getTree().getColumns();
    IPreferenceStore pref = RabbitUI.getDefault().getPreferenceStore();
    String[] widthStr = pref.getString(getWidthPreferenceString()).split(",");
    for (int i = 0; (i < widthStr.length) && (i < columns.length); i++) {
      try {
        int width = Integer.parseInt(widthStr[i]);
        columns[i].setWidth(width);
      } catch (NumberFormatException e) {
        // Leave the column to its default width.
      }
    }
  }

  /** Saves the state of the page. */
  protected void saveState() {
    TreeColumn[] columns = getViewer().getTree().getColumns();
    List<Integer> widths = Lists.newArrayListWithCapacity(columns.length);
    for (TreeColumn column : columns) {
      widths.add(column.getWidth());
    }
    IPreferenceStore pref = RabbitUI.getDefault().getPreferenceStore();
    pref.setValue(getWidthPreferenceString(), Joiner.on(',').join(widths));
  }

  private String getWidthPreferenceString() {
    return getClass().getSimpleName() + '.' + "ColumnWidths";
  }
}
