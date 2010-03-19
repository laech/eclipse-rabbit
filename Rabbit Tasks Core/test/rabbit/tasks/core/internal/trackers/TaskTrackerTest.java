package rabbit.tasks.core.internal.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Before;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;
import rabbit.core.internal.trackers.AbstractPartTrackerTest;
import rabbit.tasks.core.events.TaskEvent;

/**
 * @see TaskTracker
 */
@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class TaskTrackerTest extends AbstractPartTrackerTest<TaskEvent> {

	private ITask task;

	@Before
	public void setUpActiveTask() {
		task = new LocalTask(System.currentTimeMillis() + "", "what?");
		task.setCreationDate(new Date());
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	@Override
	protected TaskEvent createEvent() {
		return new TaskEvent(Calendar.getInstance(), 187, "fileId", task);
	}

	@Override
	protected TaskTracker createTracker() {
		return new TaskTracker();
	}

	@Override
	protected boolean hasSamePart(TaskEvent event, IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) part;
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			String id = RabbitCore.getDefault().getResourceManager().getId(
					file.getFullPath().toString());
			return event.getFileId().equals(id);
		} else {
			return false;
		}
	}

	@Override
	protected void internalAssertAccuracy(TaskEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		// 1/10 of a second is acceptable?
		assertTrue(durationInMillis - 100 <= event.getDuration());
		assertTrue(durationInMillis + 100 >= event.getDuration());
		assertTrue(start.compareTo(event.getTime()) <= 0);
		assertTrue(end.compareTo(event.getTime()) >= 0);
		assertEquals(size, tracker.getData().size());
		IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
		assertEquals(event.getFileId(), RabbitCore.getDefault().getResourceManager().getId(
				file.getFullPath().toString()));
		
		assertEquals(task, event.getTask());
	}

}
