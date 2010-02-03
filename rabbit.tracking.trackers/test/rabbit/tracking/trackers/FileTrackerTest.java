package rabbit.tracking.trackers;

import java.util.Calendar;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.tracking.event.FileEvent;

public class FileTrackerTest extends AbstractPartTrackerTest<FileEvent> {

	@Override
	protected void assertAccuracy(FileEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {
		
		Assert.assertTrue(durationInMillis - 10 <= event.getDuration());
		Assert.assertTrue(durationInMillis + 20 >= event.getDuration());
		Assert.assertTrue(start.compareTo(event.getTime()) <= 0);
		Assert.assertTrue(end.compareTo(event.getTime()) >= 0);
		Assert.assertEquals(size, tracker.getData().size());
		Assert.assertEquals(event.getFile(), ((IEditorPart) part).getEditorInput().getAdapter(IFile.class));
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, getFileForTesting());
	}

	@Override
	protected FileTracker createTracker() {
		return new FileTracker();
	}

}
