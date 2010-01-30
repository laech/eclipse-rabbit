package rabbit.tracking.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.event.WorkbenchEvent;

public class PartTrackerTest extends TrackerTest<WorkbenchEvent> {

	private IWorkbenchWindow win;
	private PartTracker tracker;

	@Before
	public void setup() {
		win = getWorkbenchWindow();
		tracker = createTracker();
	}

	public IWorkbenchWindow getWorkbenchWindow() {

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				win = wb.getActiveWorkbenchWindow();
			}
		});
		return win;
	}

	@Test
	public void testAccuracy() throws InterruptedException, PartInitException {

		IPerspectiveDescriptor newPers = win.getWorkbench()
				.getPerspectiveRegistry().getPerspectives()[0];
		win.getActivePage().setPerspective(newPers);

		String partId = "org.eclipse.ui.views.ProblemView";
		IWorkbenchPart newPart = win.getActivePage().showView(partId);

		// Test enable then disable:

		Calendar start = Calendar.getInstance();
		tracker.setEnabled(true);
		TimeUnit.MILLISECONDS.sleep(35);
		tracker.setEnabled(false);
		Calendar end = Calendar.getInstance();
		WorkbenchEvent event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 35, 1, start, end);

		// Test partActivated then partDeactivated:
		// these two methods are always called when changing views.

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(25);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 25, 1, start, end);

		// Test partActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(70);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 70, 1, start, end);

		// Test windowOpened then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(60);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 60, 1, start, end);

		// Test windowOpened then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(10);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 10, 1, start, end);
		
		// Test windowOpened then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(20);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 20, 1, start, end);
		
		// Test windowActivated then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(30);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 30, 1, start, end);

		// Test windowActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(40);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 40, 1, start, end);
		
		// Test windowActivated then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(50);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPers, newPart, 50, 1, start, end);
	}

	private void assertAccuracy(WorkbenchEvent event,
			IPerspectiveDescriptor pers, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end) {

		assertEquals(size, tracker.getData().size());
		assertEquals(pers, event.getPerspective());
		assertEquals(part, event.getWorkbenchPart());
		assertTrue(start.compareTo(event.getTime()) <= 0);
		assertTrue(end.compareTo(event.getTime()) >= 0);
		assertTrue(durationInMillis - 10 <= event.getDuration());
		assertTrue(durationInMillis + 20 >= event.getDuration());
	}

	@Override
	protected PartTracker createTracker() {
		return new PartTracker();
	}

	@Override
	protected WorkbenchEvent createEvent() {
		return new WorkbenchEvent(Calendar.getInstance(), 10,
				getWorkbenchWindow());
	}
}
