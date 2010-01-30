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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import rabbit.tracking.ui.pages.PageExtension;

public class RabbitView extends ViewPart {

	private Map<IPage, Composite> pages;
	
	private FormToolkit toolkit;
	private StackLayout stackLayout;
	private Composite displayComposite;

	public RabbitView() {
		pages = new HashMap<IPage, Composite>();
		toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
		stackLayout = new StackLayout();
	}

	@Override
	public void createPartControl(Composite parent) {

		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new FormLayout());

//		 setContentDescription("Rabbit Metrics");

		FormData fd = new FormData();
		fd.width = 1;
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 200);
		fd.bottom = new FormAttachment(100, 0);
		final Sash sash = new Sash(form.getBody(), SWT.VERTICAL);
		sash.setBackground(toolkit.getColors().getBorderColor());
		sash.setLayoutData(fd);
		sash.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				((FormData) sash.getLayoutData()).left = new FormAttachment(0,
						e.x);
				sash.getParent().layout();
			}
		});

		// Extension list:

		FormData leftData = new FormData();
		leftData.top = new FormAttachment(0, 0);
		leftData.left = new FormAttachment(0, 0);
		leftData.right = new FormAttachment(sash, 0);
		leftData.bottom = new FormAttachment(100, 0);
		Composite left = toolkit.createComposite(form.getBody());
		left.setLayout(new FillLayout());
		left.setLayoutData(leftData);
		createTree(left);

		// Displaying area:

		FormData rightData = new FormData();
		rightData.top = new FormAttachment(0, 0);
		rightData.left = new FormAttachment(sash, 0);
		rightData.right = new FormAttachment(100, 0);
		rightData.bottom = new FormAttachment(100, 0);
		displayComposite = toolkit.createComposite(form.getBody());
		displayComposite.setLayout(stackLayout);
		displayComposite.setLayoutData(rightData);
		
	}

	private void createTree(Composite parent) {

		parent.setLayout(new FillLayout());
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
					 display(page.getPage());
				}
			}
		});

		viewer.setInput(Activator.getDefault().getPages());
		viewer.expandAll();
	}
	
	private void display(IPage page) {
		
		Composite cmp = null;
		if (page != null) {
			cmp = pages.get(page);
			if (cmp == null) {
				cmp = toolkit.createComposite(displayComposite);
				cmp.setLayout(new FillLayout());
				page.createContents(cmp);
				pages.put(page, cmp);
			}
		}

		stackLayout.topControl = cmp;
		displayComposite.layout();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
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

		private Map<ImageDescriptor, Image> images 
					= new HashMap<ImageDescriptor, Image>();

		@Override
		public Image getImage(Object element) {
//			if (element instanceof PageExtension) {
//				return ((PageExtension) element).getImage();
//			}
			return super.getImage(element);
		}

		@Override
		public void dispose() {
			
			for (Image img : images.values()) {
				img.dispose();
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
