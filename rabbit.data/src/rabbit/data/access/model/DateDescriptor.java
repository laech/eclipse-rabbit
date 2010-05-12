package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * Data descriptor with a date.
 */
public class DateDescriptor {

  @Nonnull
  private final LocalDate date;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @throws NullPointerException If date is null.
   */
  public DateDescriptor(@Nonnull LocalDate date) {
    checkNotNull(date, "Date cannot be null.");
    this.date = date;
  }

  @Override
  public int hashCode() {
    return getDate().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    DateDescriptor des = (DateDescriptor) obj;
    return des.getDate().equals(getDate());
  }

  /**
   * Gets the date of the data.
   * 
   * @return The date, never null.
   */
  @Nonnull
  public final LocalDate getDate() {
    return date;
  }
}
