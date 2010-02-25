package rabbit.core.internal;

import java.text.DecimalFormat;
import java.text.Format;

/**
 * Utility class that converters milliseconds to other time units.
 */
public class MillisConverter {

	private static int SECOND = 1000;
	private static int MINUTE = SECOND * 60;
	private static int HOUR = MINUTE * 60;

	private static Format timeFormat = new DecimalFormat("#00");

	/**
	 * Formats the given duration into a human readable string.
	 * 
	 * @param millis
	 *            The duration in milliseconds.
	 * @return A human readable string representing the duration.
	 */
	public static String toDefaultString(long millis) {
		int hours = (int) (millis / HOUR);
		millis = millis % HOUR;

		int minutes = (int) (millis / MINUTE);
		millis = millis % MINUTE;

		int seconds = (int) (millis / SECOND);

		StringBuilder result = new StringBuilder();
		if (hours > 0) {
			result.append(hours);
			result.append(" hr ");
		}

		if (minutes > 0) {
			if (hours > 0) {
				result.append(timeFormat.format(minutes));
			} else {
				result.append(minutes);
			}
			result.append(" min ");
			result.append(timeFormat.format(seconds));
		} else {
			if (hours > 0) {
				result.append(timeFormat.format(minutes));
				result.append(" min ");
				result.append(timeFormat.format(seconds));
			} else {
				result.append(seconds);
			}
		}
		result.append(" s");

		return result.toString();
	}

	/**
	 * Converts to hours.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in hours.
	 */
	public static double toHours(long millis) {
		return millis / (double) HOUR;
	}

	/**
	 * Converts to minutes.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in minutes.
	 */
	public static double toMinutes(long millis) {
		return millis / (double) MINUTE;
	}

	/**
	 * Converts to seconds.
	 * 
	 * @param millis
	 *            Time in milliseconds
	 * @return Time in seconds.
	 */
	public static double toSeconds(long millis) {
		return millis / (double) SECOND;
	}
}
