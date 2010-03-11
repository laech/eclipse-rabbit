package rabbit.core.internal.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Test;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;
import rabbit.core.events.FileEvent;

/**
 * Test for {@link FileTracker}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class FileTrackerTest extends AbstractPartTrackerTest<FileEvent> {

	@Override
	protected void internalAssertAccuracy(FileEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		// 1/10 of a second is acceptable?
		Assert.assertTrue(durationInMillis - 100 <= event.getDuration());
		Assert.assertTrue(durationInMillis + 100 >= event.getDuration());
		Assert.assertTrue(start.compareTo(event.getTime()) <= 0);
		Assert.assertTrue(end.compareTo(event.getTime()) >= 0);
		Assert.assertEquals(size, tracker.getData().size());
		IFile file = (IFile) ((IEditorPart) part).getEditorInput().getAdapter(IFile.class);
		Assert.assertEquals(event.getFileId(), RabbitCore.getDefault().getResourceManager().getId(file.getFullPath().toString()));
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, "someId");
	}

	@Override
	protected FileTracker createTracker() {
		return new FileTracker();
	}

	@Override
	protected boolean hasSamePart(FileEvent event, IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) part;
			IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
			String id = RabbitCore.getDefault().getResourceManager().getId(file.getFullPath().toString());
			return event.getFileId().equals(id);
		} else {
			return false;
		}
	}

	@Test
	public void testNewWindow() {
		long sleepDuration = 15;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		openNewWindow();
		IEditorPart editor = openNewEditor();
		uiSleep(sleepDuration);
		openNewEditor();
		long end = System.currentTimeMillis();

		// One for the original window,
		// one for the newly opened window's default active view,
		// But both are views, not editors,so they are not added,
		// one for the newly opened editor.
		assertEquals(1, tracker.getData().size());

		Iterator<FileEvent> it = tracker.getData().iterator();
		FileEvent event = it.next();
		assertTrue(hasSamePart(event, editor));
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue((end - start) >= event.getDuration());

		bot.activeShell().close();
	}

}
