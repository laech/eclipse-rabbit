package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.joda.time.LocalDate;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Data descriptor for a file event.
 */
public class FileDataDescriptor extends ValueDescriptor {

  @Nonnull
  private final IPath filePath;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param value The duration of the event, in milliseconds.
   * @param filePath The path of the file.
   * @throws NullPointerException If date is null, or filePath is null.
   * @throws IllegalArgumentException If value < 0;
   */
  public FileDataDescriptor(@Nonnull LocalDate date, long value,
      @Nonnull IPath filePath) {
    super(date, value);
    checkNotNull(filePath, "File ID cannot be null");
    this.filePath = filePath;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getFilePath());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    FileDataDescriptor des = (FileDataDescriptor) obj;
    return des.getDate().equals(getDate()) && des.getValue() == getValue()
        && des.getFilePath().equals(getFilePath());
  }

  /**
   * Finds the file from the workspace that has the file path of this object.
   * 
   * @return The file handle, or {@code null} if the path is invalid for a
   *         workspace file.
   * @see #getFilePath()
   */
  @CheckForNull
  public IFile findFile() {
    if (getFilePath().segmentCount() >= 2) {
      return ResourcesPlugin.getWorkspace().getRoot().getFile(getFilePath());
    } else {
      return null;
    }
  }

  /**
   * Gets the path of the file.
   * 
   * @return The path, never null.
   */
  @Nonnull
  public IPath getFilePath() {
    return filePath;
  }

  /**
   * @return The duration in milliseconds.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }
}
