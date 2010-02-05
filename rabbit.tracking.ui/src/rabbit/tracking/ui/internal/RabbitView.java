package rabbit.tracking.ui.internal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.IPage;

/**
 * A view to show metrics.
 */
public class RabbitView extends ViewPart implements Observer {

	private Map<IPage, Composite> pages;
	private Map<IPage, Boolean> pageUpdateStatuses;

	private FormToolkit toolkit;
	private StackLayout stackLayout;
	private Form displayForm;
	private DateTime fromDateTime;
	private DateTime toDateTime;
	private DisplayPreference displayPref;

	/**
	 * Constructs a new view.
	 */
	public RabbitView() {
		pageUpdateStatuses = new HashMap<IPage, Boolean>();
		pages = new HashMap<IPage, Composite>();
		toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
		stackLayout = new StackLayout();
		displayPref = new DisplayPreference();
		displayPref.addObserver(this);
	}

	@Override public void createPartControl(Composite parent) {
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
			@Override public void handleEvent(Event e) {
				((FormData) sash.getLayoutData()).left = new FormAttachment(0, e.x);
				sash.getParent().layout();
			}
		});

		// Extension list:
		FormData leftData = new FormData();
		leftData.top = new FormAttachment(0, 0);
		leftData.left = new FormAttachment(0, 0);
		leftData.right = new FormAttachment(sash, 0);
		leftData.bottom = new FormAttachment(100, 0);
		Form left = toolkit.createForm(form.getBody());
		left.setText("Metrics");
		left.setLayoutData(leftData);
		left.getBody().setLayout(new FillLayout());
		MetricsPanel list = new MetricsPanel(this);
		list.createContents(left.getBody());

		// Displaying area:
		FormData rightData = new FormData();
		rightData.top = new FormAttachment(0, 0);
		rightData.left = new FormAttachment(sash, 0);
		rightData.right = new FormAttachment(100, 0);
		rightData.bottom = new FormAttachment(100, 0);
		displayForm = toolkit.createForm(form.getBody());
		displayForm.setLayoutData(rightData);
		displayForm.setText("Statistics");
		displayForm.getBody().setLayout(stackLayout);
		displayForm.setToolBarVerticalAlignment(SWT.TOP);
		createToolBarItems(displayForm.getToolBarManager());
	}

	/**
	 * Creates the tool bar items.
	 * 
	 * @param toolBar The tool bar.
	 */
	protected void createToolBarItems(IToolBarManager toolBar) {
		final SelectionListener update = new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				if (e.widget == fromDateTime) {
					Calendar calendar = displayPref.getStartDate();
					updateDate(calendar, fromDateTime);
					displayPref.setStartDate(calendar);
				} else if (e.widget == toDateTime) {
					Calendar calendar = displayPref.getEndDate();
					updateDate(calendar, toDateTime);
					displayPref.setEndDate(calendar);
				}
			}
		};

		toolBar.add(new ControlContribution("fromDateTime") {
			@Override protected Control createControl(Composite parent) {
				fromDateTime = new DateTime(parent, SWT.DROP_DOWN);
				fromDateTime.setToolTipText("Selects the start date for the data to be displayed.");
				fromDateTime.addSelectionListener(update);
				updateDateTime(fromDateTime, displayPref.getStartDate());
				return fromDateTime;
			}
		});

		toolBar.add(new ControlContribution("separator") {
			@Override protected Control createControl(Composite parent) {
				// Really we just want some space, not an actual separator.
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				return separator;
			}
		});

		toolBar.add(new ControlContribution("toDateTime") {
			@Override protected Control createControl(Composite parent) {
				toDateTime = new DateTime(parent, SWT.DROP_DOWN);
				toDateTime.setToolTipText("Selects the end date for the data to be displayed.");
				toDateTime.addSelectionListener(update);
				updateDateTime(toDateTime, displayPref.getEndDate());
				return toDateTime;
			}
		});

		toolBar.update(true);
	}

	/**
	 * Updates the widget with the data from the date.
	 * 
	 * @param widget The widget to be updated.
	 * @param date The date to get the data from.
	 */
	protected static void updateDateTime(DateTime widget, Calendar date) {
		widget.setYear(date.get(Calendar.YEAR));
		widget.setMonth(date.get(Calendar.MONTH));
		widget.setDay(date.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Updates the date with the data from the widget.
	 * 
	 * @param date The date to be updated.
	 * @param widget The widget to get the data from.
	 */
	protected static void updateDate(Calendar date, DateTime widget) {
		date.set(Calendar.YEAR, widget.getYear());
		date.set(Calendar.MONTH, widget.getMonth());
		date.set(Calendar.DAY_OF_MONTH, widget.getDay());
	}

	/**
	 * Displays the given page.
	 * 
	 * @param page The page to display.
	 */
	protected void display(IPage page) {

		Composite cmp = null;
		if (page != null) {
			cmp = pages.get(page);
			if (cmp == null) {
				cmp = toolkit.createComposite(displayForm.getBody());
				cmp.setLayout(new FillLayout());
				page.createContents(cmp);
				pages.put(page, cmp);
			}

			Boolean updated = pageUpdateStatuses.get(page);
			if (updated == null || updated == false) {
				page.update(displayPref);
				pageUpdateStatuses.put(page, Boolean.TRUE);
			}
		}

		stackLayout.topControl = cmp;
		displayForm.getBody().layout();
	}

	@Override public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	@Override public void update(Observable o, Object arg) {
		if (o != displayPref) {
			return;
		}

		updateDateTime(fromDateTime, displayPref.getStartDate());
		updateDateTime(toDateTime, displayPref.getEndDate());

		// Mark all invisible pages as "not yet updated":
		for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
			boolean isVisible = stackLayout.topControl == entry.getValue();
			if (isVisible) {
				entry.getKey().update(displayPref);
			}
			pageUpdateStatuses.put(entry.getKey(), Boolean.valueOf(isVisible));
		}
	}

	@Override public void setFocus() {}
}
