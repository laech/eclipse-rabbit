package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

/**
 * Data descriptor for perspective events.
 */
public class PerspectiveDataDescriptor extends ValueDescriptor {

  private final String perspectiveId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration in milliseconds.
   * @param persectiveId The ID of the perspective.
   * @throws NullPointerException If date is null, or perspectiveId is null.
   * @throws IllegalArgumentException If duration < 0.
   */
  public PerspectiveDataDescriptor(LocalDate date, long duration,
      String perspectiveId) {
    super(date, duration);
    checkNotNull(perspectiveId, "Perspective ID cannot be null");
    this.perspectiveId = perspectiveId;
  }

  @Override
  public int hashCode() {
    return (getDate().hashCode() + (int) getValue() + getPerspectiveId()
        .hashCode()) % 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    PerspectiveDataDescriptor des = (PerspectiveDataDescriptor) obj;
    return des.getDate().equals(getDate())
        && des.getPerspectiveId().equals(getPerspectiveId())
        && des.getValue() == getValue();
  }

  /**
   * Gets the ID of the perspective.
   * 
   * @return The ID of the perspective.
   */
  public String getPerspectiveId() {
    return perspectiveId;
  }

  /**
   * @return The duration in milliseconds.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }
}
