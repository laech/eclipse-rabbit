package rabbit.ui.internal;

import java.util.Collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import rabbit.ui.internal.util.PageDescriptor;

/**
 * A panel containing a collection of available metrics.
 */
public class MetricsPanel {

	private static class TreeContentProvider implements ITreeContentProvider {
		private static final Object[] EMPTY_ARRAY = new Object[0];

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(Object o) {

			if (o instanceof Collection<?>)
				return ((Collection<?>) o).toArray();

			else if (o instanceof PageDescriptor)
				return ((PageDescriptor) o).getChildren().toArray();

			else
				return EMPTY_ARRAY;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object o) {
			return (o instanceof Collection<?>) || (o instanceof PageDescriptor);
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private static class TreeLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object o) {
			return ((PageDescriptor) o).getPage().getImage();
		}

		@Override
		public String getText(Object o) {
			return ((PageDescriptor) o).getName();
		}

	}

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
		final TreeViewer viewer = new TreeViewer(parent, SWT.SINGLE);
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
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
