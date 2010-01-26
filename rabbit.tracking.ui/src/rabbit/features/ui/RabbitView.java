package rabbit.features.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

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
		createTable(cmp);
	}

	private void createTable(Composite parent) {
		Table table = new Table(parent, SWT.NONE);
		table.setHeaderVisible(true);
		table.addListener(SWT.EraseItem, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (((event.detail & SWT.SELECTED) != 0)) {
					event.detail &= ~SWT.SELECTED;
				}
				else if ((event.detail & SWT.MouseHover) != 0) {
					event.detail &= ~SWT.MouseHover;
				}
				if ((event.detail & SWT.FocusIn) != 0) {
					event.detail &= ~SWT.FocusIn;
				}
			}
		});

		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("Command Name");
		nameColumn.setWidth(200);

		TableColumn descriptionColumn = new TableColumn(table, SWT.NONE);
		descriptionColumn.setText("Description");
		descriptionColumn.setWidth(400);

		TableColumn countColumn = new TableColumn(table, SWT.NONE);
		countColumn.setText("Count");
		countColumn.setWidth(100);
	}

	private void createTree(Composite parent) {
		final Tree tree = new Tree(parent, SWT.NONE);
		tree.setHeaderVisible(true);

		final TreeColumn column = new TreeColumn(tree, SWT.NONE);
		column.setText("Metrics");
		tree.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				column.setWidth(tree.getBounds().width);
			}
		});

		TreeItem i = new TreeItem(tree, SWT.NONE);
		i.setText("Metrics");
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
