package rabbit.ui.internal;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class DateTimeButton {

	private final Format format = DateFormat.getDateInstance();

	private FormToolkit toolkit;
	private Shell shell;
	private DateTime dateTime;
	private Button button;
	private final Calendar calendar;

	private final IAction okAction = new Action() {
		@Override
		public void run() {
			shell.setVisible(false);
			calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
			button.setText(format.format(calendar.getTime()));
			button.setSelection(false);
			shell.setVisible(false);
		}
	};

	private final IAction cancelAction = new Action() {
		@Override
		public void run() {
			shell.setVisible(false);
			button.setSelection(false);
		}
	};

	private final IAction openAction = new Action() {
		@Override
		public void run() {
			RabbitView.updateDateTime(dateTime, calendar);
			Rectangle bounds = button.getBounds();
			Point location = button.getParent().toDisplay(bounds.x, bounds.y + bounds.height);
			shell.setLocation(location);
			shell.setVisible(true);
			shell.setActive();
		}
	};

	private Shell parentShell;

	DateTimeButton(Shell parentShell, Calendar calendar, FormToolkit toolkit) {
		this.parentShell = parentShell;
		this.calendar = calendar;
		this.toolkit = toolkit;
	}

	Button createContents(final Composite parent) {
		String text = "    " + format.format(calendar.getTime()) + "    ";
		button = toolkit.createButton(parent, text, SWT.TOGGLE | SWT.FLAT);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		button.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!shell.isVisible()) {
					openAction.run();
				} else {
					cancelAction.run();
				}
			}
		});

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
				button.setText(format.format(date.getTime()));
			}
		});

		createStatusbar(parent);

		shell.pack();
		return button;
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