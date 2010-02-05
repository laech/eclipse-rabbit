package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;

/**
 * Represents a file event.
 */
public class FileEvent extends ContinuousEvent {

	private IFile file;

	/**
	 * Constructs a new event.
	 * 
	 * @param time The end time of the event.
	 * @param duration The duration of the event, in milliseconds.
	 * @param f The file.
	 */
	public FileEvent(Calendar time, long duration, IFile f) {
		super(time, duration);
		setFile(f);
	}

	/**
	 * Gets the file.
	 * 
	 * @return The file.
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * Sets the file. 
	 * TODO: check for null?
	 * 
	 * @param file The file.
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

}
