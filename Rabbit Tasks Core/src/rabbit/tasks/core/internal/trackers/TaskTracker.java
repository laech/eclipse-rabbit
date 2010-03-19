package rabbit.tasks.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.RabbitCore;
import rabbit.core.internal.trackers.AbstractPartTracker;
import rabbit.core.storage.IStorer;
import rabbit.tasks.core.events.TaskEvent;
import rabbit.tasks.core.internal.storage.xml.TaskEventStorer;

public class TaskTracker extends AbstractPartTracker<TaskEvent> {

	@Override
	protected TaskEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		ITask task = TasksUi.getTaskActivityManager().getActiveTask();
		if (task == null) {
			return null;
		}

		if (p instanceof IEditorPart == false) {
			return null;
		}

		IFile f = (IFile) ((IEditorPart) p).getEditorInput().getAdapter(IFile.class);
		if (f == null) {
			return null;
		}

		String fileId = RabbitCore.getDefault().getResourceManager().insert(
					f.getFullPath().toString());
		return new TaskEvent(time, duration, fileId, task);
	}

	@Override
	protected IStorer<TaskEvent> createDataStorer() {
		return TaskEventStorer.getInstance();
	}

}
