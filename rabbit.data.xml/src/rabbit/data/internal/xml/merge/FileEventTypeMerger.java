package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.FileEventType;

import com.google.common.base.Objects;

/**
 * Merger for {@link FileEventType}.
 */
public class FileEventTypeMerger extends AbstractMerger<FileEventType> {

  @Override
  protected FileEventType doMerge(FileEventType t1, FileEventType t2) {
    t1.setDuration(t1.getDuration() + t2.getDuration());
    return t1;
  }

  @Override
  public boolean doIsMergeable(FileEventType t1, FileEventType t2) {
    return Objects.equal(t1.getFileId(), t2.getFileId());
  }

}
