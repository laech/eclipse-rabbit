package rabbit.tasks.core.internal.storage.xml;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventType;
import rabbit.tasks.core.TaskId;

public class TaskDataAccessor extends
		AbstractAccessor<Map<TaskId, Map<String, Long>>, TaskEventType, TaskEventListType> {

	@Override
	protected Map<TaskId, Map<String, Long>> filter(List<TaskEventListType> data) {
		Map<TaskId, Map<String, Long>> result = new HashMap<TaskId, Map<String, Long>>();
		for (TaskEventListType list : data) {
			for (TaskEventType type : list.getTaskEvent()) {

				String handleId = type.getTaskId().getHandleId();
				Date creationDate = type.getTaskId().getCreationDate()
						.toGregorianCalendar().getTime();
				TaskId id = new TaskId(handleId, creationDate);

				Map<String, Long> map = result.get(id);
				if (map == null) {
					map = new HashMap<String, Long>();
					result.put(id, map);
				}

				Long value = map.get(type.getFileId());
				if (value == null) {
					value = 0L;
				}
				map.put(type.getFileId(), type.getDuration() + value);
			}
		}
		return result;
	}

	@Override
	protected Collection<TaskEventListType> getCategories(EventListType doc) {
		return doc.getTaskEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.TASK_STORE;
	}
}
