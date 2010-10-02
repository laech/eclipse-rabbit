package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for a file event.
 */
public class FileDataDescriptor extends DurationDescriptor {

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
  public FileDataDescriptor(LocalDate date, Duration duration, IPath filePath) {
    super(date, duration);
    this.filePath = checkNotNull(filePath);
  }

  /**
   * Finds the file from the workspace that has the file path of this object.
   * 
   * @return The file handle, or {@code null} if the path is invalid for a
   *         workspace file.
   * @see #getFilePath()
   */
  public final IFile findFile() {
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
  public final IPath getFilePath() {
    return filePath;
  }
}
