package rabbit.core.internal.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Iterator;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Test;
import org.junit.runner.RunWith;

import rabbit.core.events.PartEvent;

/**
 * Test for {@link PartTracker}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PartTrackerTest extends AbstractPartTrackerTest<PartEvent> {

	@Override
	protected PartTracker createTracker() {
		return new PartTracker();
	}

	@Override
	protected PartEvent createEvent() {
		return new PartEvent(Calendar.getInstance(), 10, getActiveWindow().getPartService().getActivePart());
	}

	@Override
	protected void internalAssertAccuracy(PartEvent event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		assertEquals(size, tracker.getData().size());
		assertEquals(part, event.getWorkbenchPart());
		assertTrue(start.compareTo(event.getTime()) <= 0);
		assertTrue(end.compareTo(event.getTime()) >= 0);

		// 1/10 of a second is acceptable?
		assertTrue(durationInMillis - 100 <= event.getDuration());
		assertTrue(durationInMillis + 100 >= event.getDuration());
	}

	@Override
	protected boolean hasSamePart(PartEvent event, IWorkbenchPart part) {
		return event.getWorkbenchPart().equals(part);
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
		// one for the newly opened editor.
		assertEquals(3, tracker.getData().size());

		Iterator<PartEvent> it = tracker.getData().iterator();
		PartEvent event = it.next();
		while (!hasSamePart(event, editor)) {
			if (!it.hasNext()) {
				fail();
			}
			event = it.next();
		}
		assertTrue(hasSamePart(event, editor));
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue((end - start) >= event.getDuration());

		bot.activeShell().close();
	}
}
