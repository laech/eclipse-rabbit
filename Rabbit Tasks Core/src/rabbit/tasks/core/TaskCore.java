package rabbit.tasks.core;

import java.util.Map;

import rabbit.core.storage.IAccessor;
import rabbit.tasks.core.internal.storage.xml.TaskDataAccessor;

/**
 * 
 */
public class TaskCore {

	private static TaskDataAccessor accessor = new TaskDataAccessor();

	/**
	 * Gets an accessor to get the data stored.
	 * 
	 * @return An accessor to get the data stored.
	 */
	public static IAccessor<Map<TaskId, Map<String, Long>>> getTaskDataAccessor() {
		return accessor;
	}
}
