package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Data descriptor for workbench part events.
 */
public class PartDataDescriptor extends ValueDescriptor {

  @Nonnull
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
  public PartDataDescriptor(@Nonnull LocalDate date, long duration,
      @Nonnull String partId) {
    super(date, duration);
    checkNotNull(partId);
    this.partId = partId;
  }
  
  /**
   * Finds the workbench part (either an editor or a view) that has the same ID
   * as {@link #getPartId()}.
   * @return The workbench part, or null if not found.
   */
  @CheckForNull
  public IWorkbenchPartDescriptor findPart() {
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
  @Nonnull
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

  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getPartId());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    PartDataDescriptor des = (PartDataDescriptor) obj;
    return des.getDate().equals(getDate())
        && des.getPartId().equals(getPartId()) && des.getValue() == getValue();
  }

}
