package rabbit.core.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import rabbit.core.events.PerspectiveEvent;

/**
 * Test for {@link PerspectiveEvent}
 */
public class PerspectiveEventTest extends ContinuousEventTest {

	IPerspectiveDescriptor pers = PlatformUI.getWorkbench()
			.getPerspectiveRegistry().getPerspectives()[1];
	private PerspectiveEvent event = createEvent(Calendar.getInstance(), 19);

	@Test
	public void testGetPerspective() {
		assertEquals(pers, event.getPerspective());
	}

	@Test
	public void testSetPerspective() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench()
				.getPerspectiveRegistry().getPerspectives()[0];
		assertFalse(event.getPerspective().equals(p));

		event.setPerspective(p);
		assertEquals(p, event.getPerspective());
	}

	@Override
	protected PerspectiveEvent createEvent(Calendar time, long duration) {
		return new PerspectiveEvent(time, duration, PlatformUI.getWorkbench()
				.getPerspectiveRegistry().getPerspectives()[1]);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withPerspectiveNull() {
		new PerspectiveEvent(Calendar.getInstance(), 10, null);
	}

	@Test(expected = NullPointerException.class)
	public void testSetPerspective_withNull() {
		event.setPerspective(null);
	}
}
