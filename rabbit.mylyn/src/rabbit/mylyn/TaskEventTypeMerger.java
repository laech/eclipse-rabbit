package rabbit.mylyn;

import rabbit.data.internal.xml.merge.AbstractMerger;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;

import static com.google.common.base.Objects.equal;

import javax.xml.datatype.XMLGregorianCalendar;

public class TaskEventTypeMerger extends AbstractMerger<TaskEventType> {

  @Override
  protected boolean doIsMergeable(TaskEventType t1, TaskEventType t2) {
    return equal(t1.getFileId(), t2.getFileId())
        && equal(t1.getTaskId().getHandleId(), t2.getTaskId().getHandleId())
        && equal(t1.getTaskId().getCreationDate(), t2.getTaskId()
            .getCreationDate());
  }

  @Override
  protected TaskEventType doMerge(TaskEventType t1, TaskEventType t2) {
    TaskIdType id = new TaskIdType();
    id.setHandleId(t1.getTaskId().getHandleId());

    // TODO tests the calendar is cloned.
    id.setCreationDate((XMLGregorianCalendar) t1.getTaskId().getCreationDate()
        .clone());

    TaskEventType result = new TaskEventType();
    result.setDuration(t1.getDuration() + t2.getDuration());
    result.setFileId(t1.getFileId());
    result.setTaskId(id);

    return result;
  }

}
