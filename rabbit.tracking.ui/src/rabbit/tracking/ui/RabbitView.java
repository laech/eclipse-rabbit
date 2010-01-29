package rabbit.tracking.ui;

import java.util.Collection;

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

	private FormToolkit toolkit;
	
	public RabbitView() {
		toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new FormLayout());

		setContentDescription("Rabbit Metrics");

		final Sash s = new Sash(form.getBody(), SWT.VERTICAL);
		s.setBackground(toolkit.getColors().getBorderColor());
		FormData fd = new FormData();
		fd.width = 1;
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 200);
		fd.bottom = new FormAttachment(100, 0);
		s.setLayoutData(fd);
		s.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				((FormData) s.getLayoutData()).left = new FormAttachment(0, e.x);
				s.getParent().layout();
			}
		});

		Composite cmp = toolkit.createComposite(form.getBody());
		cmp.setLayout(new FillLayout());
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(0, 0);
		fd.right = new FormAttachment(s, 0);
		fd.bottom = new FormAttachment(100, 0);
		cmp.setLayoutData(fd);
		createTree(cmp);

		cmp = toolkit.createComposite(form.getBody());
		cmp.setLayout(new FillLayout());
		fd = new FormData();
		fd.top = new FormAttachment(0, 0);
		fd.left = new FormAttachment(s, 0);
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		cmp.setLayoutData(fd);
//		createTable(cmp);
	}

	private void createTree(Composite parent) {
		
		TreeViewer viewer = new TreeViewer(parent, SWT.NONE);
		viewer.setContentProvider(new ITreeContentProvider() {
			
			private final Object[] emptyArray = new Object[0];

			@Override
			public Object[] getChildren(Object o) {
				
				if (o instanceof Collection<?>) {
					return ((Collection<?>) o).toArray();
					
				} else if (o instanceof PageExtension) {
					return ((PageExtension) o).getChildren().toArray();
				}
				return emptyArray;
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
		});
		
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof PageExtension) {
					return ((PageExtension) element).getName();
				}
				return super.getText(element);
			}
			
		});
		
		final Tree tree = viewer.getTree();//new Tree(parent, SWT.NONE);
		tree.setHeaderVisible(true);

		final TreeColumn column = new TreeColumn(tree, SWT.NONE);
		column.setText("Metrics");
		tree.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				column.setWidth(tree.getBounds().width);
			}
		});
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
//					PageExtension page = (PageExtension) ((IStructuredSelection) selection).getFirstElement();
//					display(page);
				}
			}
			
		});

		viewer.setInput(Activator.getDefault().getPages());
		viewer.expandAll();
	}
	
	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

}
