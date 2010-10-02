package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

/**
 * Data descriptor with a date.
 */
public class DateDescriptor {

  private final LocalDate date;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @throws NullPointerException If date is null.
   */
  public DateDescriptor(LocalDate date) {
    this.date = checkNotNull(date, "Date cannot be null.");
  }

  /**
   * Gets the date of the data.
   * 
   * @return The date, never null.
   */
  public final LocalDate getDate() {
    return date;
  }
}
