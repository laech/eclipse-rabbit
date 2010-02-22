package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rabbit.core.internal.storage.xml.AbstractStorer.OBJECT_FACTORY;

import java.util.Calendar;
import java.util.List;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public class PerspectiveEventStorerTest
		extends AbstractStorerTest2<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

	private PerspectiveEventStorer storer = create();

	@Override
	public void testHasSameId_typeAndEvent() {
		PerspectiveEventType x1 = OBJECT_FACTORY.createPerspectiveEventType();
		x1.setPerspectiveId("paId");

		PerspectiveEventType x2 = OBJECT_FACTORY.createPerspectiveEventType();
		x2.setPerspectiveId(x1.getPerspectiveId());

		assertTrue(storer.hasSameId(x1, x2));

		x2.setPerspectiveId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_typeAndType() {
		PerspectiveEvent e = createEvent();

		PerspectiveEventType x = OBJECT_FACTORY.createPerspectiveEventType();
		x.setPerspectiveId(e.getPerspective().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPerspectiveId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	protected PerspectiveEventStorer create() {
		return new PerspectiveEventStorer();
	}

	@Override
	protected PerspectiveEvent createEvent() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0];
		return new PerspectiveEvent(Calendar.getInstance(), 194, p);
	}

	@Override
	protected PerspectiveEvent createEvent2() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[1];
		return new PerspectiveEvent(Calendar.getInstance(), 11094, p);
	}

	@Override
	protected List<PerspectiveEventType> getEventTypes(PerspectiveEventListType type) {
		return type.getPerspectiveEvent();
	}

	@Override
	protected boolean isEqual(PerspectiveEventType type, PerspectiveEvent event) {
		boolean isEqual = false;
		isEqual = type.getPerspectiveId().equals(event.getPerspective().getId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override
	protected void mergeValue(PerspectiveEvent main, PerspectiveEvent tmp) {
		main.setDuration(main.getDuration() + tmp.getDuration());
	}

	@Override
	protected long getValue(PerspectiveEventType type) {
		return type.getDuration();
	}

	@Override
	protected long getValue(PerspectiveEvent event) {
		return event.getDuration();
	}

}
