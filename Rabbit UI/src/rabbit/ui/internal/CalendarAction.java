package rabbit.ui.internal;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class CalendarAction extends Action {
	
	public static void create(IToolBarManager toolBar, Shell parentShell, Calendar calendar) {
		create(toolBar, parentShell, calendar, "  ", "  ");
	}
	
	public static void create(IToolBarManager toolBar, Shell parentShell, Calendar calendar, String prefix, String suffix) {
		toolBar.add(new CalendarAction(parentShell, calendar, prefix, suffix));
	}

	private final Format format = new SimpleDateFormat("yyyy-MM-dd");
	private final Calendar calendar;
	private final String prefix;
	private final String suffix;
	
	private DateTime dateTime;
	private Shell shell;
	

	private CalendarAction(Shell parentShell, Calendar calendar, String prefix, String suffix) {
		super("", SWT.CHECK);
		this.calendar = calendar;
		this.prefix = prefix;
		this.suffix = suffix;
		
		if (prefix == null) {
			prefix = "";
		}
		if (suffix == null) {
			suffix = "";
		}
		
		create(parentShell);
	}

	private void ok() {
		shell.setVisible(false);
		calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
		setText(getFormattedText());
		setChecked(false);
		shell.setVisible(false);
	}

	private void cancel() {
		shell.setVisible(false);
		setChecked(false);
	}

	private void open(Event e) {
		RabbitView.updateDateTime(dateTime, calendar);
		Rectangle bounds = ((ToolItem) e.widget).getBounds();
		Point location = ((ToolItem) e.widget).getParent().toDisplay(bounds.x, bounds.y + bounds.height);
		shell.setLocation(location);
		shell.setVisible(true);
		shell.setActive();
	}

	@Override
	public void runWithEvent(Event event) {
		if (!shell.isVisible()) {
			open(event);
		} else {
			cancel();
		}
	}

	private String getFormattedText() {
		return getFormattedText(calendar);
	}

	private String getFormattedText(Calendar calendar) {
		return prefix + format.format(calendar.getTime()) + suffix;
	}

	private void create(Shell parentShell) {
		setText(getFormattedText());

		GridLayout layout = new GridLayout();
		layout.marginHeight = -1; // better looking
		layout.marginWidth = -1;
		layout.verticalSpacing = 0;
		shell = new Shell(parentShell, SWT.TOOL);
		shell.setLayout(layout);
		shell.addListener(SWT.Deactivate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				cancel();
			}
		});

		dateTime = new DateTime(shell, SWT.CALENDAR);
		dateTime.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				ok();
			}
		});
		dateTime.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Calendar date = new GregorianCalendar(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
				setText(getFormattedText(date));
			}
		});

		Composite statusbar = new Composite(shell, SWT.NONE);
		statusbar.setBackground(statusbar.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		statusbar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		statusbar.setLayout(new GridLayout());

		Hyperlink today = new Hyperlink(statusbar, SWT.NONE);
		today.setText("Today");
		today.setBackground(statusbar.getBackground());
		today.setForeground(today.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		today.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		today.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				RabbitView.updateDateTime(dateTime, Calendar.getInstance());
				ok();
			}
		});

		shell.pack();
	}
}