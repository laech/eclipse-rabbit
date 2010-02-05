package rabbit.tracking.storage.xml;

import rabbit.tracking.storage.xml.PartDataAccessor;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

/**
 * Test for {@link PartDataAccessor}
 */
public class PartDataAccessorTest extends AbstractXmlAccessorTest<PartEventType, PartEventListType> {

	@Override protected PartDataAccessor create() {
		return new PartDataAccessor();
	}

	@Override protected PartEventListType createListType() {
		return objectFactory.createPartEventListType();
	}

	@Override protected PartEventType createXmlType() {
		return objectFactory.createPartEventType();
	}

	@Override protected void setId(PartEventType type, String id) {
		type.setPartId(id);
	}

	@Override protected void setUsage(PartEventType type, long usage) {
		type.setDuration(usage);
	}

}
