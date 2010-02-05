package rabbit.tracking.storage.xml;

import rabbit.tracking.storage.xml.schema.PerspectiveEventListType;
import rabbit.tracking.storage.xml.schema.PerspectiveEventType;


public class PerspectiveDataAccessorTest extends AbstractXmlAccessorTest<PerspectiveEventType, PerspectiveEventListType> {

	@Override protected PerspectiveDataAccessor create() {
		return new PerspectiveDataAccessor();
	}

	@Override protected PerspectiveEventListType createListType() {
		return objectFactory.createPerspectiveEventListType();
	}

	@Override protected PerspectiveEventType createXmlType() {
		return objectFactory.createPerspectiveEventType();
	}

	@Override protected void setId(PerspectiveEventType type, String id) {
		type.setPerspectiveId(id);
	}

	@Override protected void setUsage(PerspectiveEventType type, long usage) {
		type.setDuration(usage);
	}

}
