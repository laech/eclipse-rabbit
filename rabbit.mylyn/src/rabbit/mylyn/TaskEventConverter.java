package rabbit.mylyn;

import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.convert.AbstractConverter;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;
import rabbit.mylyn.events.TaskEvent;

import java.util.GregorianCalendar;

// TODO test
public class TaskEventConverter extends
    AbstractConverter<TaskEvent, TaskEventType> {

  @Override
  protected TaskEventType doConvert(TaskEvent event) {
    GregorianCalendar creationDate = new GregorianCalendar();
    creationDate.setTime(event.getTask().getCreationDate());

    TaskIdType id = new TaskIdType();
    id.setCreationDate(DatatypeUtil.toXmlDateTime(creationDate));
    id.setHandleId(event.getTask().getHandleIdentifier());

    TaskEventType type = new TaskEventType();
    type.setDuration(event.getDuration());
    type.setFileId(event.getFileId());
    type.setTaskId(id);
    return type;
  }

}
