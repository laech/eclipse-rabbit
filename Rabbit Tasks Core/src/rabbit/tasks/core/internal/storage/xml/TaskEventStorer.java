package rabbit.tasks.core.internal.storage.xml;

import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDateTime;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.mylyn.tasks.core.ITask;

import rabbit.core.internal.storage.xml.AbstractStorer;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventType;
import rabbit.core.internal.storage.xml.schema.events.TaskIdType;
import rabbit.tasks.core.events.TaskEvent;

public final class TaskEventStorer extends
		AbstractStorer<TaskEvent, TaskEventType, TaskEventListType> {

	private static final TaskEventStorer INSTANCE = new TaskEventStorer();

	/**
	 * Gets the shared instance of this class.
	 * 
	 * @return The shared instance.
	 */
	public static TaskEventStorer getInstance() {
		return INSTANCE;
	}

	private TaskEventStorer() {
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.TASK_STORE;
	}

	@Override
	protected List<TaskEventListType> getXmlTypeCategories(EventListType events) {
		return events.getTaskEvents();
	}

	@Override
	protected boolean hasSameId(TaskEventType x, TaskEvent e) {
		ITask task = e.getTask();
		GregorianCalendar creationDate = new GregorianCalendar();
		creationDate.setTime(task.getCreationDate());
		return x.getFileId().equals(e.getFileId())
				&& x.getTaskId().getHandleId().equals(task.getHandleIdentifier())
				&& x.getTaskId().getCreationDate().equals(
						toXMLGregorianCalendarDateTime(creationDate));
	}

	@Override
	protected boolean hasSameId(TaskEventType x1, TaskEventType x2) {
		return x1.getFileId().equals(x2.getFileId())
				&& x1.getTaskId().getHandleId().equals(x2.getTaskId().getHandleId())
				&& x1.getTaskId().getCreationDate().equals(x2.getTaskId().getCreationDate());
	}

	@Override
	protected void merge(TaskEventListType main, TaskEvent e) {
		merge(main.getTaskEvent(), e);
	}

	@Override
	protected void merge(TaskEventListType main, TaskEventListType data) {
		merge(main.getTaskEvent(), data.getTaskEvent());
	}

	@Override
	protected void merge(TaskEventType main, TaskEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(TaskEventType main, TaskEventType x) {
		main.setDuration(main.getDuration() + x.getDuration());
	}

	@Override
	protected TaskEventType newXmlType(TaskEvent e) {
		GregorianCalendar creationDate = new GregorianCalendar();
		creationDate.setTime(e.getTask().getCreationDate());

		TaskIdType id = objectFactory.createTaskIdType();
		id.setCreationDate(toXMLGregorianCalendarDateTime(creationDate));
		id.setHandleId(e.getTask().getHandleIdentifier());

		TaskEventType type = objectFactory.createTaskEventType();
		type.setDuration(e.getDuration());
		type.setFileId(e.getFileId());
		type.setTaskId(id);
		return type;
	}

	@Override
	protected TaskEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		TaskEventListType type = objectFactory.createTaskEventListType();
		type.setDate(date);
		return type;
	}

}
