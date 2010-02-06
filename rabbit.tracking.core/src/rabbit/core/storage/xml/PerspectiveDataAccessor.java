package rabbit.core.storage.xml;

import java.util.Collection;

import rabbit.core.internal.storage.xml.AbstractXmlAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public class PerspectiveDataAccessor extends AbstractXmlAccessor<PerspectiveEventType, PerspectiveEventListType> {

	@Override
	protected Collection<PerspectiveEventListType> getCategories(EventListType doc) {
		return doc.getPerspectiveEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

	@Override
	protected String getId(PerspectiveEventType e) {
		return e.getPerspectiveId();
	}

	@Override
	protected long getUsage(PerspectiveEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
		return list.getPerspectiveEvent();
	}

}
