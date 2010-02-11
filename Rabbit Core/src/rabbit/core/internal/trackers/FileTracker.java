package rabbit.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.RabbitCore;
import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.FileEventStorer;

public class FileTracker extends AbstractPartTracker<FileEvent> {

	public FileTracker() {
		super();
	}

	@Override
	protected FileEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		if (p instanceof IEditorPart) {
			IFile f = (IFile) ((IEditorPart) p).getEditorInput().getAdapter(IFile.class);
			if (f != null) {
				String id = RabbitCore.getDefault().getResourceManager().insert(f.getFullPath().toString());
				return new FileEvent(time, duration, id);
			}
		}
		return null;
	}

	@Override
	protected FileEventStorer createDataStorer() {
		return new FileEventStorer();
	}
}
