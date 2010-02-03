package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;

public class FileEvent extends ContinuousEvent {
	
	private IFile file;

	public FileEvent(Calendar time, long duration, IFile f) {
		super(time, duration);
		setFile(f);
	}

	public IFile getFile() {
		return file;
	}

	public void setFile(IFile file) {
		this.file = file;
	}

}
