package rabbit.tracking.ui;

import java.util.Calendar;
import java.util.Observable;

/**
 * Contains preferences for displaying data.
 */
public final class DisplayPreference extends Observable {

	private Calendar startDate;
	private Calendar endDate;

	/** Constructor. */
	public DisplayPreference() {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -7);
		setStartDate(start);
		setEndDate(Calendar.getInstance());
	}

	/**
	 * Gets a copy of the start date.
	 * 
	 * @return A copy of the start date.
	 */
	public Calendar getStartDate() {
		return (Calendar) startDate.clone();
	}

	/**
	 * Sets the start date.
	 * 
	 * @param date The start date.
	 * @throws NullPointerException If the parameter is null.
	 */
	public void setStartDate(Calendar date) {
		if (date == null)
			throw new NullPointerException("Date cannot be null.");
		if (date.equals(startDate))
			return;
		startDate = date;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets a copy of the end date.
	 * 
	 * @return A copy of the end date.
	 */
	public Calendar getEndDate() {
		return (Calendar) endDate.clone();
	}

	/**
	 * Sets the end date.
	 * 
	 * @param date The end date.
	 * @throws NullPointerException If the parameter is null.
	 */
	public void setEndDate(Calendar date) {
		if (date == null)
			throw new NullPointerException("Date cannot be null.");
		if (date.equals(endDate))
			return;
		endDate = date;
		setChanged();
		notifyObservers();
	}
}
