package rabbit.ui;

import java.util.Calendar;

import rabbit.ui.internal.RabbitUI;

/**
 * Contains preferences for displaying data.
 */
public final class DisplayPreference {

	private Calendar startDate;
	private Calendar endDate;

	/** Constructor. */
	public DisplayPreference() {
		endDate = Calendar.getInstance();
		startDate = (Calendar) endDate.clone();
		startDate.add(Calendar.DAY_OF_MONTH, -RabbitUI.getDefault().getDefaultDisplayDatePeriod());
	}

	/**
	 * Gets the start date for the data to be displayed.
	 * @return The start date.
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Gets the end date for the data to be displayed.
	 * @return The end date.
	 */
	public Calendar getEndDate() {
		return endDate;
	}
}
