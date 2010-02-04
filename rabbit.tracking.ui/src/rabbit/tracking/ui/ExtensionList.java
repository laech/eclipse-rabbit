package rabbit.tracking.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.tracking.ui.pages.PageExtension;

public class ExtensionList {
	
	private RabbitView view;
	
	public ExtensionList(RabbitView v) {
		view = v;
	}
	
	public void createContents(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.NONE);
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		
		final Tree tree = viewer.getTree();// new Tree(parent, SWT.NONE);
		tree.setHeaderVisible(true);

		final TreeColumn title = new TreeColumn(tree, SWT.NONE);
		title.setText("Metrics");
		
		tree.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				title.setWidth(tree.getBounds().width);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					 PageExtension page = (PageExtension)
					 	((IStructuredSelection) selection).getFirstElement();
					 view.display(page.getPage());
				}
			}
		});

		viewer.setInput(Activator.getDefault().getPages());
		viewer.expandAll();
	}
	private static class TreeContentProvider implements ITreeContentProvider {

		private static final Object[] EMPTY_ARRAY = new Object[0];

		@Override
		public Object[] getChildren(Object o) {

			if (o instanceof Collection<?>) {
				return ((Collection<?>) o).toArray();

			} else if (o instanceof PageExtension) {
				return ((PageExtension) o).getChildren().toArray();
			}
			return EMPTY_ARRAY;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object o) {

			if ((o instanceof Collection<?>) || (o instanceof PageExtension)) {
				return true;
			}
			return false;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private static class TreeLabelProvider extends LabelProvider {

		private Map<PageExtension, Image> images 
					= new HashMap<PageExtension, Image>();

		@Override
		public Image getImage(Object element) {
			if (element instanceof PageExtension) {
				PageExtension p = (PageExtension) element;
				Image img = images.get(p);
				if (img == null) {
					ImageDescriptor des = p.getImageDescriptor();
					img = (des != null) ? des.createImage() : null;
					images.put(p, img);
				}
				return img;
			}
			return super.getImage(element);
		}

		@Override
		public void dispose() {
			for (Image img : images.values()) {
				if (img != null) {
					img.dispose();
				}
			}
			super.dispose();
		}

		@Override
		public String getText(Object element) {
			if (element instanceof PageExtension) {
				return ((PageExtension) element).getName();
			}
			return super.getText(element);
		}

	}
}
