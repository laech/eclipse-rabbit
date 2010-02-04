package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.OBJECT_FACTORY;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.toXMLGregorianCalendarDate;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.event.PerspectiveEvent;
import rabbit.tracking.storage.xml.schema.PerspectiveEventListType;
import rabbit.tracking.storage.xml.schema.PerspectiveEventType;

public class PerspectiveEventStorerTest
		extends
		AbstractXmlStorerTest2<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

	private PerspectiveEventStorer storer = create();
	private PerspectiveEvent event = createEvent();

	@Override
	public void testHasSameId_workbenchEventTypeAndEvent() {
		PerspectiveEventType x1 = OBJECT_FACTORY.createPerspectiveEventType();
		x1.setPerspectiveId("paId");
		
		PerspectiveEventType x2 = OBJECT_FACTORY.createPerspectiveEventType();
		x2.setPerspectiveId(x1.getPerspectiveId());
		
		assertTrue(storer.hasSameId(x1, x2));
		
		x2.setPerspectiveId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_workbenchEventTypeAndWorkbenchEventType() {
		PerspectiveEvent e = createEvent();

		PerspectiveEventType x = OBJECT_FACTORY.createPerspectiveEventType();
		x.setPerspectiveId(e.getPerspective().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPerspectiveId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	public void testMerge_xmlListTypeAndEvent() {
		PerspectiveEvent e = createEvent();
		PerspectiveEventType x = storer.newXmlType(e);
		
		PerspectiveEventListType main = OBJECT_FACTORY.createPerspectiveEventListType();
		main.getPerspectiveEvent().add(x);
		
		long totalDuration = x.getDuration() * 2;
		storer.merge(main, e);
		assertEquals(1, main.getPerspectiveEvent().size());
		assertEquals(totalDuration, main.getPerspectiveEvent().get(0).getDuration());
	}

	@Override
	public void testMerge_xmlListTypeAndxmlListType() {
		PerspectiveEvent e = createEvent();
		PerspectiveEventType x = storer.newXmlType(e);
		
		PerspectiveEventListType main = OBJECT_FACTORY.createPerspectiveEventListType();
		main.getPerspectiveEvent().add(x);
		
		PerspectiveEventListType tmp = OBJECT_FACTORY.createPerspectiveEventListType();
		tmp.getPerspectiveEvent().add(x);
		
		long totalDuration = x.getDuration() * 2;
		storer.merge(main, tmp);
		assertEquals(1, main.getPerspectiveEvent().size());
		assertEquals(totalDuration, main.getPerspectiveEvent().get(0).getDuration());
	}

	@Override
	public void testMerge_xmlTypeAndEvent() {
		PerspectiveEventType type = storer.newXmlType(event);
		storer.merge(type, event);
		assertEquals(event.getDuration() * 2, type.getDuration());
		assertEquals(event.getPerspective().getId(), type.getPerspectiveId());
	}

	@Override
	public void testMerge_xmlTypeAndXmlType() {
		PerspectiveEventType t1 = storer.newXmlType(event);
		PerspectiveEventType t2 = storer.newXmlType(event);
		storer.merge(t1, t2);
		assertEquals(event.getDuration() * 2, t1.getDuration());
		assertEquals(event.getPerspective().getId(), t1.getPerspectiveId());
	}

	@Override
	public void testNewXmlTypeHolderXMLGregorianCalendar() {
		XMLGregorianCalendar cal = toXMLGregorianCalendarDate(Calendar.getInstance());
		PerspectiveEventListType type = storer.newXmlTypeHolder(cal);
		assertEquals(cal, type.getDate());
	}

	@Override
	public void testNewXmlTypeT() {
		PerspectiveEventType type = storer.newXmlType(event);
		assertEquals(event.getPerspective().getId(), type.getPerspectiveId());
		assertEquals(event.getDuration(), type.getDuration());
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

}
