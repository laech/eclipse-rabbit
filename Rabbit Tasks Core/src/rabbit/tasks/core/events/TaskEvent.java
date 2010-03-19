package rabbit.tasks.core.events;

import java.util.Calendar;

import org.eclipse.mylyn.tasks.core.ITask;

import rabbit.core.events.FileEvent;

/**
 * Represents a task event.
 */
public class TaskEvent extends FileEvent {

	private ITask task;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration of the event, in milliseconds.
	 * @param fileId
	 *            The id of the file.
	 * @param task
	 *            The task.
	 * @throws IllegalArgumentException
	 *             If duration is negative, or file Id is an empty string or
	 *             contains whitespace only.
	 * @throws NullPointerException
	 *             If time is null, or file id is null, or task is null.
	 */
	public TaskEvent(Calendar time, long duration, String fileId, ITask task) {
		super(time, duration, fileId);
		setTask(task);
	}

	/**
	 * Gets the task.
	 * 
	 * @return The task.
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * Sets the task.
	 * 
	 * @param task
	 *            The task.
	 * @throws NullPointerException
	 *             If task is null.
	 */
	public void setTask(ITask task) {
		if (task == null) {
			throw new NullPointerException();
		}
		this.task = task;
	}

}
