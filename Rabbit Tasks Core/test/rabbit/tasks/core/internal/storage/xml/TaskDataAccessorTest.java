package rabbit.tasks.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbit.core.internal.storage.xml.AbstractAccessorTest;
import rabbit.core.internal.storage.xml.DatatypeConverter;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventType;
import rabbit.core.internal.storage.xml.schema.events.TaskIdType;
import rabbit.tasks.core.TaskId;

/**
 * @see TaskDataAccessor
 */
public class TaskDataAccessorTest extends
		AbstractAccessorTest<Map<TaskId, Map<String, Long>>, TaskEventType, TaskEventListType> {

	@Override
	protected void assertValues(Map<TaskId, Map<String, Long>> data, EventListType events) {
		Map<TaskId, Map<String, Long>> map = new HashMap<TaskId, Map<String, Long>>();
		for (TaskEventListType list : events.getTaskEvents()) {
			for (TaskEventType type : list.getTaskEvent()) {

				String handleId = type.getTaskId().getHandleId();
				Date creationDate = type.getTaskId().getCreationDate()
						.toGregorianCalendar().getTime();
				TaskId taskId = new TaskId(handleId, creationDate);

				Map<String, Long> fileMap = map.get(taskId);
				if (fileMap == null) {
					fileMap = new HashMap<String, Long>();
					map.put(taskId, fileMap);
				}

				Long value = fileMap.get(type.getFileId());
				if (value == null) {
					value = 0L;
				}
				fileMap.put(type.getFileId(), value + type.getDuration());
			}
		}
		assertEquals(map, data);
	}

	@Override
	protected TaskDataAccessor create() {
		return new TaskDataAccessor();
	}

	@Override
	protected TaskEventListType createListType() {
		return objectFactory.createTaskEventListType();
	}

	@Override
	protected TaskEventType createXmlType() {
		TaskIdType id = objectFactory.createTaskIdType();
		id.setCreationDate(DatatypeConverter
				.toXMLGregorianCalendarDateTime(new GregorianCalendar()));
		id.setHandleId("abcdef");

		TaskEventType type = objectFactory.createTaskEventType();
		type.setTaskId(id);
		return type;
	}

	@Override
	protected List<TaskEventType> getXmlTypes(TaskEventListType list) {
		return list.getTaskEvent();
	}

	@Override
	protected void setId(TaskEventType type, String id) {
		type.setFileId(id);
	}

	@Override
	protected void setUsage(TaskEventType type, long usage) {
		type.setDuration(usage);
	}

}
