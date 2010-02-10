package rabbit.ui.internal;

public class MillisConverter {

	public static double SECOND = 1000;
	public static double MINUTE = SECOND * 60;
	public static double HOUR = MINUTE * 60;

	public static double toSeconds(long millis) {
		return millis / SECOND;
	}

	public static double toMinutes(long millis) {
		return millis / MINUTE;
	}

	public static double toHours(long millis) {
		return millis / HOUR;
	}

}
