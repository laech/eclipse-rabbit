package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * A data descriptor with a value.
 */
public class DurationDescriptor extends DateDescriptor {

  private final Duration duration;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration.
   * @throws NullPointerException If any of the arguments are null.
   */
  public DurationDescriptor(LocalDate date, Duration duration) {
    super(date);
    this.duration = checkNotNull(duration);
  }

  /**
   * @return The duration.
   */
  public final Duration getDuration() {
    return duration;
  }
}
