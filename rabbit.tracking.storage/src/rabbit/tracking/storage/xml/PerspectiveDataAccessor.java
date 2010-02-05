package rabbit.tracking.storage.xml;

import java.util.Collection;

import rabbit.tracking.storage.xml.internal.DataStore;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.PerspectiveEventListType;
import rabbit.tracking.storage.xml.schema.PerspectiveEventType;

public class PerspectiveDataAccessor extends AbstractXmlAccessor<PerspectiveEventType, PerspectiveEventListType> {

	@Override protected Collection<PerspectiveEventListType> getCategories(EventListType doc) {
		return doc.getPerspectiveEvents();
	}

	@Override protected IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

	@Override protected String getId(PerspectiveEventType e) {
		return e.getPerspectiveId();
	}

	@Override protected long getUsage(PerspectiveEventType e) {
		return e.getDuration();
	}

	@Override protected Collection<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
		return list.getPerspectiveEvent();
	}

}
