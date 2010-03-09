package rabbit.core.storage.xml;

import java.util.Collection;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public class PerspectiveDataAccessor extends AbstractAccessor<PerspectiveEventType, PerspectiveEventListType> {

	@Override
	public Collection<PerspectiveEventListType> getCategories(EventListType doc) {
		return doc.getPerspectiveEvents();
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

	@Override
	public String getId(PerspectiveEventType e) {
		return e.getPerspectiveId();
	}

	@Override
	public long getUsage(PerspectiveEventType e) {
		return e.getDuration();
	}

	@Override
	public Collection<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
		return list.getPerspectiveEvent();
	}

}
