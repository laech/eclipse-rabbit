package rabbit.ui.internal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import rabbit.core.RabbitCore;
import rabbit.ui.DisplayPreference;
import rabbit.ui.pages.IPage;

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
	/**
	 * A preference for updating the pages, do not attach pages to this
	 * preference as observers because we don't want them to update themselves
	 * automatically, we want to update them manually.
	 */
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

		final Image metricsImg = RabbitUI.imageDescriptorFromPlugin(RabbitUI.PLUGIN_ID, "resources/metrics.png").createImage();
		left.setImage(metricsImg);
		left.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				metricsImg.dispose();
			}
		});

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

		final Image statImg = RabbitUI.imageDescriptorFromPlugin(RabbitUI.PLUGIN_ID, "resources/stat.png").createImage();
		displayForm.setImage(statImg);
		displayForm.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				statImg.dispose();
			}
		});

		String text = "Saves data about the current workbench and updates the pages";
		ImageDescriptor icon = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED);
		getViewSite().getActionBars().getToolBarManager().add(new Action(text, icon) {
			@Override
			public void run() {
				RabbitCore.getDefault().saveCurrentData();
				update(displayPref, null);
			}
		});
	}

	/**
	 * Creates the tool bar items.
	 * 
	 * @param toolBar
	 *            The tool bar.
	 */
	protected void createToolBarItems(IToolBarManager toolBar) {
		String text = "Apply";
		ImageDescriptor icon = RabbitUI.imageDescriptorFromPlugin("org.eclipse.ui.browser", "icons/elcl16/nav_refresh.gif"); //$NON-NLS-1$//$NON-NLS-2$
		final IAction updateAction = new Action(text, icon) {
			@Override
			public void run() {
				update();
				setEnabled(false);
			}
		};
		updateAction.setEnabled(false);

		final SelectionListener update = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == fromDateTime) {
					Calendar calendar = displayPref.getStartDate();
					if (!isSameDate(calendar, fromDateTime)) {
						updateAction.setEnabled(true);
						updateDate(calendar, fromDateTime);
						displayPref.setStartDate(calendar);
					}
				} else if (e.widget == toDateTime) {
					Calendar calendar = displayPref.getEndDate();
					if (!isSameDate(calendar, toDateTime)) {
						updateAction.setEnabled(true);
						updateDate(calendar, toDateTime);
						displayPref.setEndDate(calendar);
					}
				}
			}
		};

		toolBar.add(new ControlContribution("rabbit.ui.fromDateTime") {
			@Override
			protected Control createControl(Composite parent) {
				fromDateTime = new DateTime(parent, SWT.DROP_DOWN | SWT.BORDER);
				fromDateTime.setToolTipText("Selects the start date for the data to be displayed.");
				fromDateTime.addSelectionListener(update);
				updateDateTime(fromDateTime, displayPref.getStartDate());
				return fromDateTime;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.separator") {
			@Override
			protected Control createControl(Composite parent) {
				// Really we just want some space, not an actual
				// separator.
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				return separator;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.toDateTime") {
			@Override
			protected Control createControl(Composite parent) {
				toDateTime = new DateTime(parent, SWT.DROP_DOWN | SWT.BORDER);
				toDateTime.setToolTipText("Selects the end date for the data to be displayed.");
				toDateTime.addSelectionListener(update);
				updateDateTime(toDateTime, displayPref.getEndDate());
				return toDateTime;
			}
		});

		toolBar.add(new ControlContribution("rabbit.ui.separator2") {
			@Override
			protected Control createControl(Composite parent) {
				// Really we just want some space, not an actual
				// separator.
				Label separator = new Label(parent, SWT.NO_BACKGROUND);
				return separator;
			}
		});
		toolBar.add(updateAction);

		toolBar.update(true);
	}

	/**
	 * Updates the widget with the data from the date.
	 * 
	 * @param widget
	 *            The widget to be updated.
	 * @param date
	 *            The date to get the data from.
	 */
	protected static void updateDateTime(DateTime widget, Calendar date) {
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
	protected static void updateDate(Calendar date, DateTime widget) {
		date.set(Calendar.YEAR, widget.getYear());
		date.set(Calendar.MONTH, widget.getMonth());
		date.set(Calendar.DAY_OF_MONTH, widget.getDay());
	}

	/**
	 * Checks whether the calendar and the widget represent the same date.
	 * 
	 * @param date
	 *            The calendar.
	 * @param widget
	 *            The widget.
	 * @return True if the date are the same, false otherwise.
	 */
	protected static boolean isSameDate(Calendar date, DateTime widget) {
		return date.get(Calendar.YEAR) == widget.getYear()
				&& date.get(Calendar.MONTH) == widget.getMonth()
				&& date.get(Calendar.DAY_OF_MONTH) == widget.getDay();
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

			Boolean updated = pageUpdateStatuses.get(page);
			if (updated == null || updated == false) {
				page.update(displayPref);
				pageUpdateStatuses.put(page, Boolean.TRUE);
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

	@Override
	public void update(Observable o, Object arg) {
		if (o != displayPref) {
			return;
		}

		updateDateTime(fromDateTime, displayPref.getStartDate());
		updateDateTime(toDateTime, displayPref.getEndDate());
		// Do not update here, update will be call when user clicks the button.
	}

	/**
	 * Updates the pages to current preference.
	 */
	private void update() {
		// Mark all invisible pages as "not yet updated":
		for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
			boolean isVisible = stackLayout.topControl == entry.getValue();
			if (isVisible) {// update current visible page.
				entry.getKey().update(displayPref);
			}
			pageUpdateStatuses.put(entry.getKey(), Boolean.valueOf(isVisible));
		}
	}

	@Override
	public void setFocus() {
	}
}
