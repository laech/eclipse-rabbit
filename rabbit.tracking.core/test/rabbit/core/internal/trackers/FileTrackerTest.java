package rabbit.core.internal.trackers;

import java.util.Calendar;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.ResourceData;
import rabbit.core.internal.trackers.FileTracker;

/**
 * Test for {@link FileTracker}
 */
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
		Assert.assertEquals(event.getFileId(), ResourceData.INSTANCE.getId(file.getFullPath()));
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, "someId");
	}

	@Override
	protected FileTracker createTracker() {
		return new FileTracker();
	}

}
