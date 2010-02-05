package rabbit.tracking.trackers;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.tracking.event.FileEvent;
import rabbit.tracking.storage.xml.FileEventStorer;

public class FileTracker extends AbstractPartTracker<FileEvent> {

	public FileTracker() {
		super();
	}

	@Override protected FileEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		if (p instanceof IEditorPart) {
			IFile f = (IFile) ((IEditorPart) p).getEditorInput().getAdapter(IFile.class);
			return new FileEvent(time, duration, f);
		}
		return null;
	}

	@Override protected FileEventStorer createDataStorer() {
		return new FileEventStorer();
	}
}
