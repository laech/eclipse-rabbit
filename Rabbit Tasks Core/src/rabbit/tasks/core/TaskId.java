package rabbit.tasks.core;

import java.util.Date;

/**
 * Represents an ID of a task.
 */
public final class TaskId {

	/*
	 * Note: Mylyn re-uses local task IDs, that is, if task A has ID = 1, then
	 * the user deletes A and creates task B, B's ID could be 1 as well.
	 * Therefore it is necessary to compare the creation date of tasks.
	 */

	private final String handleId;
	private final Date creationDate;

	/**
	 * Constructs a new ID.
	 * 
	 * @param handleId
	 *            The handle identifier of the task.
	 * @param creationDate
	 *            The creation date of the task.
	 * @throws NullPointerException
	 *             If any of the parameter is null.
	 * @throws IllegalArgumentException
	 *             If the handle ID is an empty string, or contains white spaces
	 *             only.
	 */
	public TaskId(String handleId, Date creationDate) {
		if (handleId == null || creationDate == null) {
			throw new NullPointerException();
		}
		if (handleId.trim().length() == 0) {
			throw new IllegalArgumentException();
		}
		this.handleId = handleId;
		this.creationDate = (Date) creationDate.clone();
	}

	/**
	 * Gets the handle identifier.
	 * 
	 * @return The handle identifier.
	 */
	public String getHandleIdentifier() {
		return handleId;
	}

	/**
	 * Gets the creation date.
	 * 
	 * @return The creation date.
	 */
	public Date getCreationDate() {
		return (Date) creationDate.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskId) {
			TaskId id = (TaskId) obj;
			return handleId.equals(id.getHandleIdentifier())
					&& creationDate.equals(id.getCreationDate());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return handleId.hashCode();
	}
}
