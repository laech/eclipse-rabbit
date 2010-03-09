package rabbit.core.internal.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public class PerspectiveEventStorer
		extends AbstractStorer<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

	@Override
	public List<PerspectiveEventListType> getXmlTypeCategories(EventListType events) {
		return events.getPerspectiveEvents();
	}

	@Override
	public boolean hasSameId(PerspectiveEventType x, PerspectiveEvent e) {
		return x.getPerspectiveId().equals(e.getPerspective().getId());
	}

	@Override
	public boolean hasSameId(PerspectiveEventType x1, PerspectiveEventType x2) {
		return x1.getPerspectiveId().equals(x2.getPerspectiveId());
	}

	@Override
	public void merge(PerspectiveEventType main, PerspectiveEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	public void merge(PerspectiveEventType main, PerspectiveEventType x) {
		main.setDuration(main.getDuration() + x.getDuration());
	}

	@Override
	public void merge(PerspectiveEventListType main, PerspectiveEvent e) {
		merge(main.getPerspectiveEvent(), e);
	}

	@Override
	public void merge(PerspectiveEventListType main, PerspectiveEventListType data) {
		merge(main.getPerspectiveEvent(), data.getPerspectiveEvent());
	}

	@Override
	public PerspectiveEventType newXmlType(PerspectiveEvent e) {
		PerspectiveEventType type = OBJECT_FACTORY.createPerspectiveEventType();
		type.setDuration(e.getDuration());
		type.setPerspectiveId(e.getPerspective().getId());
		return type;
	}

	@Override
	public PerspectiveEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		PerspectiveEventListType type = OBJECT_FACTORY.createPerspectiveEventListType();
		type.setDate(date);
		return type;
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

}
