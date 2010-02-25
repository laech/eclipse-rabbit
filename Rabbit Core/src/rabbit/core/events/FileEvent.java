package rabbit.core.events;

import java.util.Calendar;

/**
 * Represents a file event. This object stores the file id instead of the file
 * itself.
 * 
 * @see rabbit.core.RabbitCore#getResourceManager()
 */
public class FileEvent extends ContinuousEvent {

	private String fileId;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration of the event, in milliseconds.
	 * @param fileId
	 *            The id of the file.
	 */
	public FileEvent(Calendar time, long duration, String fileId) {
		super(time, duration);
		setFileId(fileId);
	}

	/**
	 * Gets the file id.
	 * 
	 * @return The file id.
	 * @see rabbit.core.RabbitCore#getResourceManager()
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * Sets the file id.
	 * 
	 * @param fileId
	 *            The file id.
	 * @see rabbit.core.RabbitCore#getResourceManager()
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
