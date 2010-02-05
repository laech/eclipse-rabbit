package rabbit.tracking.trackers;

import java.util.Calendar;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.tracking.event.FileEvent;

/**
 * Test for {@link FileTracker}
 */
public class FileTrackerTest extends AbstractPartTrackerTest<FileEvent> {

	@Override protected void internalAssertAccuracy(FileEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		// 1/10 of a second is acceptable?
		Assert.assertTrue(durationInMillis - 100 <= event.getDuration());
		Assert.assertTrue(durationInMillis + 100 >= event.getDuration());
		Assert.assertTrue(start.compareTo(event.getTime()) <= 0);
		Assert.assertTrue(end.compareTo(event.getTime()) >= 0);
		Assert.assertEquals(size, tracker.getData().size());
		Assert.assertEquals(event.getFile(), ((IEditorPart) part).getEditorInput().getAdapter(IFile.class));
	}

	@Override protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, getFileForTesting());
	}

	@Override protected FileTracker createTracker() {
		return new FileTracker();
	}

}
