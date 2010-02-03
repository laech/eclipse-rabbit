package rabbit.tracking.storage.xml;

import java.util.Collection;

import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

public class PartDataAccessor extends AbstractXmlAccessor<PartEventType, PartEventListType> {

	@Override
	protected Collection<PartEventListType> getCategories(EventListType doc) {
		return doc.getPartEvents();
	}

	@Override
	protected String getId(PartEventType e) {
		return e.getPartId();
	}

	@Override
	protected long getUsage(PartEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}
}
