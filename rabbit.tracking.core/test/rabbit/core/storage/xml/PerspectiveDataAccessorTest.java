package rabbit.core.storage.xml;

import rabbit.core.internal.storage.xml.AbstractXmlAccessorTest;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;
import rabbit.core.storage.xml.PerspectiveDataAccessor;

public class PerspectiveDataAccessorTest extends AbstractXmlAccessorTest<PerspectiveEventType, PerspectiveEventListType> {

	@Override
	protected PerspectiveDataAccessor create() {
		return new PerspectiveDataAccessor();
	}

	@Override
	protected PerspectiveEventListType createListType() {
		return objectFactory.createPerspectiveEventListType();
	}

	@Override
	protected PerspectiveEventType createXmlType() {
		return objectFactory.createPerspectiveEventType();
	}

	@Override
	protected void setId(PerspectiveEventType type, String id) {
		type.setPerspectiveId(id);
	}

	@Override
	protected void setUsage(PerspectiveEventType type, long usage) {
		type.setDuration(usage);
	}

}
