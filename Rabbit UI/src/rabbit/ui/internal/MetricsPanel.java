package rabbit.ui.internal;

import java.util.Collection;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import rabbit.ui.internal.util.PageDescriptor;

/**
 * A panel containing a collection of available metrics.
 */
public class MetricsPanel {

	private RabbitView view;

	/**
	 * Constructor.
	 * 
	 * @param v
	 *            The parent view.
	 */
	public MetricsPanel(RabbitView v) {
		view = v;
	}

	/**
	 * Creates the content.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public void createContents(Composite parent) {
		final TreeViewer viewer = new TreeViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(new AbstractTreeContentProvider() {

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
				return (o instanceof PageDescriptor) && !((PageDescriptor) o).getChildren().isEmpty();
			}
		});

		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		viewer.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				return ((PageDescriptor) element).getPage().getImage();
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

		});

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
					PageDescriptor page = (PageDescriptor) ((IStructuredSelection) selection).getFirstElement();
					view.display(page.getPage());
				}
			}
		});

		viewer.setInput(RabbitUI.getDefault().getPages());
		viewer.expandAll();
	}
}
