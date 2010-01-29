package rabbit.tracking.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

public class DisplayPreference extends Observable {
	
	// The time period:
	
	private Calendar startDate;
	private Calendar endDate;
	
	private DisplayPreference() {
		
		// Setup the default values:
		
		Calendar from = new GregorianCalendar();
		from.set(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH) - 7);
		
		Calendar to = new GregorianCalendar();
		to.set(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH) + 1);
		
		startDate = from;
		endDate   = to;
	}
	
	/**
	 * Gets the start date for the data to be displayed.
	 * 
	 * @return The start date for the data to be displayed.
	 */
	public Calendar getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date for the data to be displayed.
	 * This method is not intended to be called by clients.
	 * @param date The new start date.
	 */
	public void setStartDate(Calendar date) {
		if (startDate == date) {
			return;
		}
		startDate = date;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Gets the end date for the data to be displayed.
	 * 
	 * @return The end date for the data to be displayed.
	 */
	public Calendar getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the end date for the data to be displayed.
	 * This method is not intended to be called by clients.
	 * @param date The new end date.
	 */
	public void setEndDate(Calendar date) {
		if (endDate == date) {
			return;
		}
		endDate = date;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Gets the shared instance of this class.
	 * @return The shared instance of this class.
	 */
	public static DisplayPreference getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	// Singleton holder
	private static class SingletonHolder {
		private static DisplayPreference INSTANCE = new DisplayPreference();
	}
}
