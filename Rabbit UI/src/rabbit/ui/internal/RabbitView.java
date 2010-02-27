package rabbit.ui.internal;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
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

import rabbit.core.RabbitCore;
import rabbit.ui.DisplayPreference;
import rabbit.ui.IPage;

/**
 * A view to show metrics.
 */
public class RabbitView extends ViewPart {

	/**
	 * A map containing page status (updated or not), if a page is not updated
	 * (value return false), then it will be updated before it's displayed (when
	 * a user clicks on a tree node).
	 */
	private Map<IPage, Boolean> pageStatus;
	private Map<IPage, Composite> pages;
	private DisplayPreference preferences;
	private FormToolkit toolkit;
	private StackLayout stackLayout;
	private Form displayForm;

	/**
	 * Constructs a new view.
	 */
	public RabbitView() {
		pages = new HashMap<IPage, Composite>();
		toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
		stackLayout = new StackLayout();
		preferences = new DisplayPreference();
		pageStatus = new HashMap<IPage, Boolean>();
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
		MetricsPanel list = new MetricsPanel(this, toolkit);
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

		Label label = toolkit.createLabel(displayForm.getBody(), null, SWT.CENTER);
		label.setImage(getTitleImage());
		stackLayout.topControl = label;
		displayForm.getBody().layout();
	}

	/**
	 * Creates the tool bar items.
	 * 
	 * @param toolBar
	 *            The tool bar.
	 */
	private void createToolBarItems(IToolBarManager toolBar) {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			createToolBarForWindowsOS(toolBar);
		} else {
			createToolBarForNonWindowsOS(toolBar);
		}

		// Really we just want some space, not an actual separator.
		toolBar.add(new ControlContribution("rabbit.ui.refreshSeparator") {
			@Override
			protected Control createControl(Composite parent) {
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				separator.setText("  ");
				return separator;
			}
		});
		String text = "Refresh";
		ImageDescriptor icon = imageDescriptorFromPlugin("org.eclipse.ui.browser", "icons/elcl16/nav_refresh.gif");
		final IAction updateAction = new Action(text, icon) {
			@Override
			public void run() {
				update();
			}
		};
		toolBar.add(updateAction);
		toolBar.update(true);
	}

	/**
	 * Creates tool bar items for Windows operating system.
	 * 
	 * @param toolBar
	 *            The tool bar.
	 */
	private void createToolBarForWindowsOS(IToolBarManager toolBar) {
		toolBar.add(new ControlContribution("rabbit.ui.fromDateTime") {
			@Override
			protected Control createControl(Composite parent) {
				final DateTime fromDateTime = new DateTime(parent, SWT.DROP_DOWN | SWT.BORDER);
				updateDateTime(fromDateTime, preferences.getStartDate());
				fromDateTime.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						updateDate(preferences.getEndDate(), fromDateTime);
					}
				});
				return fromDateTime;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.dateTimeSeparator") {
			@Override
			protected Control createControl(Composite parent) {
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				separator.setText("  ");
				return separator;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.toDateTime") {
			@Override
			protected Control createControl(Composite parent) {
				final DateTime toDateTime = new DateTime(parent, SWT.DROP_DOWN | SWT.BORDER);
				toDateTime.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						updateDate(preferences.getStartDate(), toDateTime);
					}
				});
				updateDateTime(toDateTime, preferences.getEndDate());
				return toDateTime;
			}
		});
	}

	/**
	 * Creates tool bar items for non windows operating systems.
	 * 
	 * @param toolBar
	 *            The tool bar.
	 */
	private void createToolBarForNonWindowsOS(IToolBarManager toolBar) {
		toolBar.add(new ControlContribution("rabbit.ui.fromButton") {
			@Override
			protected Control createControl(Composite parent) {
				return new DateTimeButton(getSite().getShell(),
						preferences.getStartDate(), toolkit).createContents(parent);
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.refreshSeparator") {
			@Override
			protected Control createControl(Composite parent) {
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				separator.setText("  ");
				return separator;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.toButton") {
			@Override
			protected Control createControl(Composite parent) {
				return new DateTimeButton(getSite().getShell(),
						preferences.getEndDate(), toolkit).createContents(parent);
			}
		});
	}

	/**
	 * Updates the widget with the data from the date.
	 * 
	 * @param widget
	 *            The widget to be updated.
	 * @param date
	 *            The date to get the data from.
	 */
	static void updateDateTime(DateTime widget, Calendar date) {
		widget.setYear(date.get(Calendar.YEAR));
		widget.setMonth(date.get(Calendar.MONTH));
		widget.setDay(date.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Updates the date with the data from the widget.
	 * 
	 * @param date
	 *            The date to be updated.
	 * @param widget
	 *            The widget to get the data from.
	 */
	static void updateDate(Calendar date, DateTime widget) {
		date.set(Calendar.YEAR, widget.getYear());
		date.set(Calendar.MONTH, widget.getMonth());
		date.set(Calendar.DAY_OF_MONTH, widget.getDay());
	}

	/**
	 * Checks whether the two calendars has the same year, month, and day of
	 * month.
	 * 
	 * @param date1
	 *            The calendar.
	 * @param date2
	 *            The other calendar.
	 * @return True if the dates has the same year, month, and day of month,
	 *         false otherwise.
	 */
	static boolean isSameDate(Calendar date1, Calendar date2) {
		return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
				&& date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
				&& date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Displays the given page.
	 * 
	 * @param page
	 *            The page to display.
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

			Boolean updated = pageStatus.get(page);
			if (updated == null || updated == false) {
				page.update(preferences);
				pageStatus.put(page, Boolean.TRUE);
			}
		}

		stackLayout.topControl = cmp;
		displayForm.getBody().layout();
	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Updates the pages to current preference.
	 */
	private void update() {
		// Sync with today's data:
		Calendar today = Calendar.getInstance();
		if (isSameDate(today, preferences.getEndDate()) || today.before(preferences.getEndDate())) {
			RabbitCore.getDefault().saveCurrentData();
		}

		// Mark all invisible pages as "not yet updated":
		for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
			boolean isVisible = stackLayout.topControl == entry.getValue();
			if (isVisible) {
				// update current visible page.
				entry.getKey().update(preferences);
			}
			pageStatus.put(entry.getKey(), Boolean.valueOf(isVisible));
		}
	}

	@Override
	public void setFocus() {
	}
}
