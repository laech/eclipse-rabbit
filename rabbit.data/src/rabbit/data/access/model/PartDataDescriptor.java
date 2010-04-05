package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

/**
 * Data descriptor for workbench part events.
 */
public class PartDataDescriptor extends ValueDescriptor {

  private final String partId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration in milliseconds.
   * @param partId The ID of the workbench part.
   * @throws NullPointerException If date is null, or partId is null.
   * @throws IllegalArgumentException If duration < 0.
   */
  public PartDataDescriptor(LocalDate date, long duration, String partId) {
    super(date, duration);
    checkNotNull(partId);
    this.partId = partId;
  }

  /**
   * Gets the ID of the workbench part.
   * 
   * @return The part ID.
   */
  public String getPartId() {
    return partId;
  }

  /**
   * @return The duration in milliseconds.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }

  // TODO test
  @Override
  public int hashCode() {
    return (getPartId().hashCode() + (int) getValue() + getDate().hashCode()) % 31;
  }
  
  //TODO test
  @Override
  public boolean equals(Object obj) {
    if (null == obj) return false;
    if (this == obj) return true;
    if (getClass() != obj.getClass()) return false;
    
    PartDataDescriptor des = (PartDataDescriptor) obj;
    return des.getDate().equals(getDate())
        && des.getPartId().equals(getPartId())
        && des.getValue() == getValue();
  }
  
}
