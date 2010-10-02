package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for workbench part events.
 */
public class PartDataDescriptor extends DurationDescriptor {

  private final String partId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration.
   * @param partId The ID of the workbench part.
   * @throws NullPointerException If date is null, or partId is null.
   * @throws IllegalArgumentException If duration < 0.
   */
  public PartDataDescriptor(LocalDate date, Duration duration, String partId) {
    super(date, duration);
    this.partId = checkNotNull(partId);
  }
  
  /**
   * Finds the workbench part (either an editor or a view) that has the same ID
   * as {@link #getPartId()}.
   * @return The workbench part, or null if not found.
   */
  public final IWorkbenchPartDescriptor findPart() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchPartDescriptor des = workbench.getViewRegistry().find(getPartId());
    if (des == null) {
      des = workbench.getEditorRegistry().findEditor(getPartId());
    }
    return des;
  }

  /**
   * Gets the ID of the workbench part.
   * 
   * @return The part ID, never null.
   */
  public final String getPartId() {
    return partId;
  }
}
