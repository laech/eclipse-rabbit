package rabbit.tracking.event;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

/**
 * Test for {@link PerspectiveEvent}
 */
public class PerspectiveEventTest extends ContinuousEventTest {

	IPerspectiveDescriptor pers = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[1];
	private PerspectiveEvent event = createEvent(Calendar.getInstance(), 19);

	@Test public void testGetPerspective() {
		assertEquals(pers, event.getPerspective());
	}

	@Test public void testSetPerspective() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0];
		assertFalse(event.getPerspective().equals(p));

		event.setPerspective(p);
		assertEquals(p, event.getPerspective());
	}

	@Override protected PerspectiveEvent createEvent(Calendar time, long duration) {
		return new PerspectiveEvent(time, duration, pers);
	}

}
