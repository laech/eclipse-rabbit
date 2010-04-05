package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

/**
 * Data descriptor for a file event.
 */
public class FileDataDescriptor extends ValueDescriptor {

  private final String fileId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param value The duration of the event, in milliseconds.
   * @throws NullPointerException If date is null, or fileId is null.
   * @throws IllegalArgumentException If value < 0;
   */
  public FileDataDescriptor(LocalDate date, long value, String fileId) {
    super(date, value);
    checkNotNull(fileId, "File ID cannot be null");
    this.fileId = fileId;
  }

  @Override
  public int hashCode() {
    return (getDate().hashCode() + (int) getValue() + getFileId().hashCode()) % 31;
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
        && des.getFileId().equals(getFileId());
  }

  /**
   * Gets the ID of the file.
   * 
   * @return The ID.
   */
  public String getFileId() {
    return fileId;
  }

  /**
   * @return The duration in milliseconds.
   */
  @Override
  public long getValue() {
    return super.getValue();
  }
}
