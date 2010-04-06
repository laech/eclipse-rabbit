package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.store.model.FileEvent;

/**
 * Converts from {@link FileEvent} to {@link FileEventType}.
 */
public class FileEventConverter extends
    AbstractConverter<FileEvent, FileEventType> {

  @Override
  protected FileEventType doConvert(FileEvent element) {
    FileEventType type = new FileEventType();
    type.setDuration(element.getDuration());
    type.setFileId(element.getFileId());
    return type;
  }

}
