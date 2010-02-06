package rabbit.core.internal.trackers;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import static org.junit.Assert.*;
import org.junit.*;

import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.trackers.PerspectiveTracker;

/**
 * Test for {@link PerspectiveTracker}
 */
public class PerspectiveTrackerTest extends AbstractTrackerTest<PerspectiveEvent> {

	private PerspectiveTracker tracker;
	private IWorkbenchWindow win;

	@Before
	public void setup() {
		tracker = createTracker();

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				win = wb.getActiveWorkbenchWindow();
			}
		});
	}

	@Test
	public void testAccuracy() throws InterruptedException {
		PerspectiveEvent e = null;
		Calendar start = null;
		Calendar end = null;
		long duration = 0;
		int size = 0;

		win.getActivePage().setPerspective(PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0]);

		// Test enable then disable:

		tracker.setEnabled(true);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 30));
		end = Calendar.getInstance();
		tracker.setEnabled(false);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test perspectiveActivated then perspectiveDeactivated:

		tracker.flushData();
		tracker.perspectiveActivated(win.getActivePage(), win.getActivePage().getPerspective());
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 25));
		end = Calendar.getInstance();
		tracker.perspectiveDeactivated(win.getActivePage(), win.getActivePage().getPerspective());
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test perspectiveActivated then windowClosed:

		tracker.flushData();
		tracker.perspectiveActivated(win.getActivePage(), win.getActivePage().getPerspective());
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 12));
		end = Calendar.getInstance();
		tracker.windowClosed(win);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowOpened then perspectiveDeactivated:

		tracker.flushData();
		tracker.windowOpened(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 21));
		end = Calendar.getInstance();
		tracker.perspectiveDeactivated(win.getActivePage(), win.getActivePage().getPerspective());
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowOpened then windowClosed:

		tracker.flushData();
		tracker.windowOpened(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 10));
		end = Calendar.getInstance();
		tracker.windowClosed(win);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowOpened then windowDeactivated:

		tracker.flushData();
		tracker.windowOpened(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 49));
		end = Calendar.getInstance();
		tracker.windowDeactivated(win);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowActivated then windowDeactivated:

		tracker.flushData();
		tracker.windowActivated(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 25));
		end = Calendar.getInstance();
		tracker.windowDeactivated(win);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowActivated then windowClosed:

		tracker.flushData();
		tracker.windowActivated(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 9));
		end = Calendar.getInstance();
		tracker.windowClosed(win);
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());

		// Test windowActivated then perspectiveDeactivated:

		tracker.flushData();
		tracker.windowActivated(win);
		start = Calendar.getInstance();
		TimeUnit.MILLISECONDS.sleep((duration = 6));
		end = Calendar.getInstance();
		tracker.perspectiveDeactivated(win.getActivePage(), win.getActivePage().getPerspective());
		size = 1;
		e = tracker.getData().iterator().next();
		internalAssertAccuracy(e, start, end, duration, size, win.getActivePage().getPerspective());
	}

	private void internalAssertAccuracy(PerspectiveEvent e, Calendar start,
			Calendar end, long duration, int size, IPerspectiveDescriptor per) {

		assertEquals(per, e.getPerspective());
		assertEquals(size, tracker.getData().size());
		assertTrue(start.compareTo(e.getTime()) <= 0);
		assertTrue(end.compareTo(e.getTime()) >= 0);
		assertTrue(duration - 100 <= e.getDuration());
		assertTrue(duration + 100 >= e.getDuration());
	}

	@Override
	protected PerspectiveEvent createEvent() {
		return new PerspectiveEvent(Calendar.getInstance(), 101, win.getActivePage().getPerspective());
	}

	@Override
	protected PerspectiveTracker createTracker() {
		return new PerspectiveTracker();
	}

}
