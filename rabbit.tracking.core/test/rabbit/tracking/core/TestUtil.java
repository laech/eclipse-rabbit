package rabbit.tracking.core;

public class TestUtil {
	
	/**
	 * Creates a new tracker for testing.
	 * @return A new tracker.
	 */
	public static ITracker newTracker() {
		
		return new ITracker() {
			
			boolean isEnabled = false;

			@Override
			public boolean isEnabled() {
				return isEnabled;
			}

			@Override
			public void setEnabled(boolean enable) {
				isEnabled = enable;
			}
			
		};
	}
}
