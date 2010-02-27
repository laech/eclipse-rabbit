package rabbit.ui.internal;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class CalendarAction extends Action {

	private final Format format = DateFormat.getDateInstance();

	private FormToolkit toolkit;
	private Shell shell;
	private DateTime dateTime;
	private final Calendar calendar;

	private final Runnable okAction = new Runnable() {
		@Override
		public void run() {
			shell.setVisible(false);
			calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
			setText(format.format(calendar.getTime()));
			setChecked(false);
			shell.setVisible(false);
		}
	};

	private final Runnable cancelAction = new Runnable() {
		@Override
		public void run() {
			shell.setVisible(false);
			setChecked(false);
		}
	};

	private final IAction openAction = new Action() {
		@Override
		public void runWithEvent(Event e) {
			RabbitView.updateDateTime(dateTime, calendar);
//			Rectangle bounds = e.getBounds();
//			Point location = ((Control)e.item).toDisplay(bounds.x, bounds.y + bounds.height);
			shell.setLocation(new Point(100, 100));
			shell.setVisible(true);
			shell.setActive();
			
		}
	};

	private Shell parentShell;

	CalendarAction(Shell parentShell, Calendar calendar, FormToolkit toolkit) {
		this.parentShell = parentShell;
		this.calendar = calendar;
		this.toolkit = toolkit;
		createAction();
	}
	
	public void runWithEvent(Event event) {
		if (!shell.isVisible()) {
			openAction.runWithEvent(event);
		} else {
			cancelAction.run();
		}
	}

	private void createAction() {
		String text = "    " + format.format(calendar.getTime()) + "    ";
		setText(text);

		GridLayout layout = new GridLayout();
		layout.marginHeight = -1; // better looking
		layout.marginWidth = -1;
		layout.verticalSpacing = 0;
		shell = new Shell(parentShell, SWT.TOOL);
		shell.setLayout(layout);
		shell.addListener(SWT.Deactivate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				cancelAction.run();
			}
		});

		dateTime = new DateTime(shell, SWT.CALENDAR);
		dateTime.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event event) {
				okAction.run();
			}
		});
		dateTime.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Calendar date = new GregorianCalendar(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
				setText(format.format(date.getTime()));
			}
		});

		createStatusbar(shell);

		shell.pack();
	}

	private void createStatusbar(Composite parent) {
		Composite statusbar = toolkit.createComposite(shell);
		statusbar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		statusbar.setLayout(new GridLayout());

		Hyperlink today = toolkit.createHyperlink(statusbar, "Select Today", SWT.NONE);
		today.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		today.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				RabbitView.updateDateTime(dateTime, Calendar.getInstance());
				okAction.run();
			}
		});
	}
}