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

import rabbit.data.access.IAccessor2;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler2;
import rabbit.ui.CellPainter;
import rabbit.ui.Preferences;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

/**
 * TODO test A page for displaying time spent working on different files.
 */
public class ResourcePage extends AbstractTreeViewerPage2 {

  public static enum ShowMode {
    FILE, FOLDER, PROJECT
  }

  private ShowMode mode = ShowMode.FILE;

  private IAccessor2<FileDataDescriptor> accessor;
  private ResourcePageContentProvider contents;
  private ResourcePageTableLabelProvider labels;

  public ResourcePage() {
    super();
    contents = new ResourcePageContentProvider(this);
    labels = new ResourcePageTableLabelProvider(contents);
    accessor = DataHandler2.getFileDataAccessor();
  }

  @Override
  public void createContents(Composite parent) {
    super.createContents(parent);
    getViewer().addFilter(new ViewerFilter() {

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        TreeNode node = (TreeNode) element;
        if (node.getValue() instanceof LocalDate) {
          return true;
        }

        switch (getShowMode()) {
        case PROJECT:
          return node.getValue() instanceof IProject;
        case FOLDER:
          return node.getValue() instanceof IContainer;
        default:
          return true;
        }
      }
    });
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
    ImageDescriptor image;

    // Action to show projects:
    IAction projAction = new Action("Show Projects", IAction.AS_RADIO_BUTTON) {
      @Override
      public void run() {
        setShowMode(ShowMode.PROJECT);
      }
    };
    image = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
    projAction.setImageDescriptor(image);

    // Action to show folders:
    IAction folderAction = new Action("Show Folders", IAction.AS_RADIO_BUTTON) {
      @Override
      public void run() {
        setShowMode(ShowMode.FOLDER);
      }
    };
    image = images.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
    folderAction.setImageDescriptor(image);

    // Action to show files:
    IAction fileAction = new Action("Show Files", IAction.AS_RADIO_BUTTON) {
      @Override
      public void run() {
        setShowMode(ShowMode.FILE);
      }
    };
    image = images.getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
    fileAction.setImageDescriptor(image);

    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(), //
        new ActionContributionItem(projAction),
        new ActionContributionItem(folderAction),
        new ActionContributionItem(fileAction), };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  public ShowMode getShowMode() {
    return mode;
  }

  public void setShowMode(ShowMode newMode) {
    if (mode == newMode) {
      return;
    }
    mode = newMode;
    getViewer().refresh();
  }

  @Override
  public void update(Preferences p) {
    Object[] elements = getViewer().getExpandedElements();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    try {
      getViewer().setExpandedElements(elements);
    } catch (IllegalArgumentException e) {
      // Just in case some of the elements are no longer valid.
    }
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 136, 177, 231);
      }
    };
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(200);
    column.addSelectionListener(createInitialComparator(viewer));

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Time Spent");
    column.setWidth(150);
    column.addSelectionListener(getValueSorter());
  }

  // TODO test
  @Override
  protected ITreeContentProvider createContentProvider() {
    return contents;
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (!(e1 instanceof TreeNode) || !(e1 instanceof TreeNode))
          return 0;

        Object element1 = ((TreeNode) e1).getValue();
        Object element2 = ((TreeNode) e2).getValue();
        if (element1 instanceof LocalDate && element2 instanceof LocalDate) {
          return ((LocalDate) element1).compareTo(((LocalDate) element2));

        } else {
          return labels.getColumnText(e1, 0).compareToIgnoreCase(
              labels.getColumnText(e2, 0));
        }
      }
    };
  }

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return labels;
  }
}
