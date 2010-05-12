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
package rabbit.ui.internal;

import rabbit.ui.internal.util.PageDescriptor;
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

/**
 * A panel containing a collection of available metrics.
 */
public class MetricsPanel {

  private RabbitView view;

  /**
   * Constructor.
   * 
   * @param v The parent view.
   */
  public MetricsPanel(RabbitView v) {
    view = v;
  }

  /**
   * Creates the content.
   * 
   * @param parent The parent composite.
   */
  public void createContents(Composite parent) {
    final TreeViewer viewer = new TreeViewer(parent, SWT.SINGLE | SWT.V_SCROLL
        | SWT.H_SCROLL);
    viewer.setContentProvider(createContentProvider());
    viewer.setComparator(new ViewerComparator());

    ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
    viewer.setLabelProvider(createLabelProvider());

    viewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent e) {
        IStructuredSelection select = (IStructuredSelection) e.getSelection();
        Object o = select.getFirstElement();
        if (((ITreeContentProvider) viewer.getContentProvider()).hasChildren(o)) {
          viewer.setExpandedState(o, !viewer.getExpandedState(o));
        }
      }
    });

    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
          PageDescriptor page = (PageDescriptor) ((IStructuredSelection) selection)
              .getFirstElement();
          view.display(page.getPage());
        }
      }
    });

    viewer.setInput(RabbitUI.getDefault().loadRootPages());
    viewer.expandAll();
  }

  private IContentProvider createContentProvider() {
    return new AbstractTreeContentProvider() {

      @Override
      public Object[] getChildren(Object o) {
        return ((PageDescriptor) o).getChildren().toArray();
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return ((Collection<?>) inputElement).toArray();
      }

      @Override
      public boolean hasChildren(Object o) {
        return (o instanceof PageDescriptor)
            && !((PageDescriptor) o).getChildren().isEmpty();
      }
    };
  }

  private CellLabelProvider createLabelProvider() {
    ILabelProvider provider =  new ColumnLabelProvider() {

      private final ImageRegistry images = new ImageRegistry();

      @Override
      public void dispose() {
        super.dispose();
        images.dispose();
      }
      
      @Override
      public void update(ViewerCell cell) {
        super.update(cell);
        cell.setText(getText(cell.getElement()));
        cell.setImage(getImage(cell.getElement()));
      }

      @Override
      public Image getImage(Object element) {
        PageDescriptor page = (PageDescriptor) element;
        if (page.getImage() == null) {
          return null;
        }
        Image image = images.get(page.getName());
        if (image == null) {
          image = page.getImage().createImage();
          images.put(page.getName(), image);
        }
        return image;
      }

      @Override
      public String getText(Object element) {
        return ((PageDescriptor) element).getName();
      }

      @Override
      public String getToolTipText(Object element) {
        return ((PageDescriptor) element).getDescription();
      }

      @Override
      public boolean useNativeToolTip(Object object) {
        return true;
      }
    };
    return new DelegatingStyledCellLabelProvider(provider, true);
  }
}
