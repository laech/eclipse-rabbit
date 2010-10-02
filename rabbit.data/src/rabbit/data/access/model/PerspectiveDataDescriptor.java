package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for perspective events.
 */
public class PerspectiveDataDescriptor extends DurationDescriptor {

  private final String perspectiveId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration.
   * @param persectiveId The ID of the perspective.
   * @throws NullPointerException If any of the arguments are null.
   */
  public PerspectiveDataDescriptor(LocalDate date, Duration duration, String perspectiveId) {
    super(date, duration);
    this.perspectiveId = checkNotNull(perspectiveId);
  }
  
  /**
   * Finds the perspective that has the same ID as {@link #getPerspectiveId()}.
   * @return The perspective, or null if not found.
   */
  public final IPerspectiveDescriptor findPerspective() {
    return PlatformUI.getWorkbench().getPerspectiveRegistry()
        .findPerspectiveWithId(getPerspectiveId());
  }

  /**
   * Gets the ID of the perspective.
   * 
   * @return The ID of the perspective, never null.
   */
  public final String getPerspectiveId() {
    return perspectiveId;
  }
}
