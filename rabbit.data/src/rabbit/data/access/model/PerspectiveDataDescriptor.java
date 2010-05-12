package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Data descriptor for perspective events.
 */
public class PerspectiveDataDescriptor extends ValueDescriptor {

  @Nonnull
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
  public PerspectiveDataDescriptor(@Nonnull LocalDate date, long duration,
      @Nonnull String perspectiveId) {
    super(date, duration);
    checkNotNull(perspectiveId, "Perspective ID cannot be null");
    this.perspectiveId = perspectiveId;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getPerspectiveId());
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
   * Finds the perspective that has the same ID as {@link #getPerspectiveId()}.
   * @return The perspective, or null if not found.
   */
  @CheckForNull
  public final IPerspectiveDescriptor findPerspective() {
    return PlatformUI.getWorkbench().getPerspectiveRegistry()
        .findPerspectiveWithId(getPerspectiveId());
  }

  /**
   * Gets the ID of the perspective.
   * 
   * @return The ID of the perspective, never null.
   */
  @Nonnull
  public final String getPerspectiveId() {
    return perspectiveId;
  }
}
