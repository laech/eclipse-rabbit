package rabbit.ui.internal.util;

/**
 * Utility class that converters milliseconds to other time units.
 */
public class MillisConverter {

	private static double SECOND = 1000;
	private static double MINUTE = SECOND * 60;
	private static double HOUR = MINUTE * 60;

	/**
	 * Converts to seconds.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in seconds.
	 */
	public static double toSeconds(long millis) {
		return millis / SECOND;
	}

	/**
	 * Converts to minutes.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in minutes.
	 */
	public static double toMinutes(long millis) {
		return millis / MINUTE;
	}

	/**
	 * Converts to hours.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in hours.
	 */
	public static double toHours(long millis) {
		return millis / HOUR;
	}

}
