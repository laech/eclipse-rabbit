package rabbit.core.internal.storage.xml;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.PartEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;
import rabbit.core.storage.xml.IDataStore;

public class PartEventStorer
		extends AbstractStorer<PartEvent, PartEventType, PartEventListType> {

	public PartEventStorer() {
	}

	@Override
	protected List<PartEventListType> getXmlTypeCategories(EventListType events) {
		return events.getPartEvents();
	}

	@Override
	protected boolean hasSameId(PartEventType x, PartEvent e) {

		boolean result = false;
		if (e.getWorkbenchPart() != null) {
			result = e.getWorkbenchPart().getSite().getId().equals(x.getPartId());
		}
		return result;
	}

	@Override
	protected boolean hasSameId(PartEventType x1, PartEventType x2) {
		return x1.getPartId().equals(x2.getPartId());
	}

	@Override
	protected void merge(PartEventListType main, PartEvent e) {
		merge(main.getPartEvent(), e);
	}

	@Override
	protected void merge(PartEventListType main, PartEventListType data) {
		merge(main.getPartEvent(), data.getPartEvent());
	}

	@Override
	protected void merge(PartEventType main, PartEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(PartEventType main, PartEventType x2) {
		main.setDuration(main.getDuration() + x2.getDuration());
	}

	@Override
	protected PartEventType newXmlType(PartEvent e) {

		PartEventType type = OBJECT_FACTORY.createPartEventType();
		type.setDuration(e.getDuration());
		type.setPartId(e.getWorkbenchPart().getSite().getId());

		return type;
	}

	@Override
	protected PartEventListType newXmlTypeHolder(XMLGregorianCalendar date) {

		PartEventListType type = OBJECT_FACTORY.createPartEventListType();
		type.setDate(date);

		return type;
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}
}
