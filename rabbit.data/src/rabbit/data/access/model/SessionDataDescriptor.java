package rabbit.data.access.model;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for session events.
 */
public class SessionDataDescriptor extends DurationDescriptor {

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration.
   * @throws NullPointerException If any of the arguments are null.
   */
  public SessionDataDescriptor(LocalDate date, Duration duration) {
    super(date, duration);
  }
}
