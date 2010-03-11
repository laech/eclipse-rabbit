/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal;

import java.text.DateFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * Associates a pop up calendar widget to a button, when the button is selected,
 * a calendar will pop up below the button to allow the user to select a date.
 */
public class DateTimeButton {

	/**
	 * Creates a pop up calendar and associates it with the button selection
	 * event.
	 * 
	 * @param button
	 *            The button.
	 * @param calendar
	 *            The calendar object to manipulate.
	 */
	public static void create(Button button, Calendar calendar) {
		create(button, calendar, "", "");
	}

	/**
	 * Creates a pop up calendar and associates it with the button selection
	 * event.
	 * 
	 * @param button
	 *            The button.
	 * @param calendar
	 *            The calendar object to manipulate.
	 * @param prefix
	 *            The prefix for the button's text.
	 * @param suffix
	 *            The suffix for the button's text.
	 */
	public static void create(Button button, Calendar calendar, String prefix, String suffix) {
		new DateTimeButton(button, calendar, prefix, suffix);
	}

	private final Format format = DateFormat.getDateInstance();
	private final Calendar calendar;
	private final String prefix;
	private final String suffix;

	private DateTime dateTime;
	private Button button;
	private Shell shell;

	private final Runnable okAction = new Runnable() {
		@Override
		public void run() {
			shell.setVisible(false);
			calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
			button.setText(getFormattedText());
			button.setSelection(false);
			shell.setVisible(false);
		}
	};

	private final Runnable cancelAction = new Runnable() {
		@Override
		public void run() {
			shell.setVisible(false);
			button.setSelection(false);
		}
	};

	private final Runnable openAction = new Runnable() {
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

	private DateTimeButton(Button button, Calendar calendar, String prefix, String suffix) {
		this.calendar = calendar;
		this.prefix = prefix;
		this.suffix = suffix;
		this.button = button;

		if (prefix == null) {
			prefix = "";
		}
		if (suffix == null) {
			suffix = "";
		}

		button.setText(getFormattedText());
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!shell.isVisible()) {
					openAction.run();
				} else {
					cancelAction.run();
				}
			}
		});

		createContents();
	}

	private void createContents() {
		GridLayout layout = new GridLayout();
		layout.marginHeight = -1; // better looking
		layout.marginWidth = -1;
		layout.verticalSpacing = 0;
		shell = new Shell(button.getShell(), SWT.TOOL);
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
				Calendar date = new GregorianCalendar(dateTime.getYear(), dateTime.getMonth(),
						dateTime.getDay());
				button.setText(getFormattedText(date));
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
				okAction.run();
			}
		});

		shell.pack();
	}

	private String getFormattedText() {
		return getFormattedText(calendar);
	}

	private String getFormattedText(Calendar calendar) {
		return prefix + format.format(calendar.getTime()) + suffix;
	}
}