package rabbit.core.events;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Calendar;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import rabbit.core.events.PartEvent;

/**
 * Test for {@link PartEvent}
 */
public class PartEventTest extends ContinuousEventTest {

	private IWorkbenchWindow win = getWorkbenchWindow();
	private IWorkbenchPart part = getWorkbenchWindow().getPartService().getActivePart();

	private PartEvent event = createEvent(Calendar.getInstance(), 10);

	/** Gets the currently active workbench window. */
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

	@Override
	protected PartEvent createEvent(Calendar time, long duration) {
		return new PartEvent(time, duration, getWorkbenchWindow()
				.getPartService().getActivePart());
	}

	@Test
	public void testWorkbenchEvent() {
		assertNotNull(event);
	}

	@Test
	public void testGetWorkbenchPart() {
		assertSame(part, event.getWorkbenchPart());
	}

	@Test
	public void testSetWorkbenchPart() {

		try {
			IWorkbenchPart newP = win.getActivePage()
					.showView("org.eclipse.ui.navigator.ProjectExplorer");
			event.setWorkbenchPart(newP);
			assertSame(newP, event.getWorkbenchPart());

		} catch (PartInitException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testContructor_withPartNull() {
		new PartEvent(Calendar.getInstance(), 10, null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetWorkbenchPart_withNull() {
		event.setWorkbenchPart(null);
	}
}
