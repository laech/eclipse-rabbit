package rabbit.tracking.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.eclipse.ui.IWorkbenchPart;

import rabbit.tracking.event.PartEvent;

/**
 * Test for {@link PartTracker}
 */
public class PartTrackerTest extends AbstractPartTrackerTest<PartEvent> {

	@Override protected PartTracker createTracker() {
		return new PartTracker();
	}

	@Override protected PartEvent createEvent() {
		return new PartEvent(Calendar.getInstance(), 10, getWorkbenchWindow().getPartService().getActivePart());
	}

	@Override protected void internalAssertAccuracy(PartEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		assertEquals(size, tracker.getData().size());
		assertEquals(part, event.getWorkbenchPart());
		assertTrue(start.compareTo(event.getTime()) <= 0);
		assertTrue(end.compareTo(event.getTime()) >= 0);

		// 1/10 of a second is acceptable?
		assertTrue(durationInMillis - 100 <= event.getDuration());
		assertTrue(durationInMillis + 100 >= event.getDuration());
	}
}
