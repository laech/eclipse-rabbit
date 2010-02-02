package rabbit.tracking.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

public class RabbitView extends ViewPart implements Observer {

	private Map<IPage, Composite> pages;
	private Map<IPage, Boolean> pageUpdateStatuses;
	
	private FormToolkit toolkit;
	private StackLayout stackLayout;
	private Composite displayComposite;

	public RabbitView() {
		pages = new HashMap<IPage, Composite>();
		pageUpdateStatuses = new HashMap<IPage, Boolean>();
		toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
		stackLayout = new StackLayout();
		
		DisplayPreference.getInstance().addObserver(this);
	}

	@Override
	public void createPartControl(Composite parent) {

		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new FormLayout());

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
		ExtensionList list = new ExtensionList(this);
		list.createContents(left);

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

	protected void display(IPage page) {
		
		Composite cmp = null;
		if (page != null) {
			cmp = pages.get(page);
			if (cmp == null) {
				cmp = toolkit.createComposite(displayComposite);
				cmp.setLayout(new FillLayout());
				page.createContents(cmp);
				pages.put(page, cmp);
			}
			
			Boolean updated = pageUpdateStatuses.get(page);
			if (updated == null || updated == false) {
				page.update(DisplayPreference.getInstance());
				pageUpdateStatuses.put(page, Boolean.TRUE);
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

	@Override
	public void update(Observable o, Object arg) {
		if (!(o instanceof DisplayPreference)) {
			return;
		}

		// Mark all invisible pages as "not yet updated":
		for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
			boolean isVisible = stackLayout.topControl == entry.getValue();
			if (isVisible) {
				entry.getKey().update((DisplayPreference) o);
			}
			pageUpdateStatuses.put(entry.getKey(), Boolean.valueOf(isVisible));
		}
	}

}
