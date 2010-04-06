package rabbit.data.access.model;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * Data descriptor for session events.
 */
public class SessionDataDescriptor extends ValueDescriptor {

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration in milliseconds.
   * @throws NullPointerException If date is null.
   * @throws IllegalArgumentException If duration < 0.
   */
  public SessionDataDescriptor(@Nonnull LocalDate date, long duration) {
    super(date, duration);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDate());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    SessionDataDescriptor des = (SessionDataDescriptor) obj;
    return des.getDate().equals(getDate()) && des.getValue() == getValue();
  }

  /**
   * @return The duration in milliseconds.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }
}
