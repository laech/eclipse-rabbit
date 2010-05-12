package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * A data descriptor with a value.
 */
public class ValueDescriptor extends DateDescriptor {

  private final long value;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param value The value of the data.
   * @throws NullPointerException If date is null.
   * @throws IllegalArgumentException If value < 0.
   */
  public ValueDescriptor(@Nonnull LocalDate date, long value) {
    super(date);
    checkArgument(value >= 0, "Duration cannot be negative");
    this.value = value;
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

    ValueDescriptor des = (ValueDescriptor) obj;
    return des.getDate().equals(getDate()) && des.getValue() == getValue();
  }

  /**
   * Gets the value of the data.
   * 
   * @return The value.
   */
  public final long getValue() {
    return value;
  }
}
