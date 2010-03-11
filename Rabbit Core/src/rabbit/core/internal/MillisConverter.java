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
