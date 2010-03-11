package rabbit.core.storage.xml;

import rabbit.core.internal.storage.xml.AbstractAccessorTest;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;
import rabbit.core.storage.xml.PartDataAccessor;

/**
 * Test for {@link PartDataAccessor}
 */
public class PartDataAccessorTest extends AbstractAccessorTest<PartEventType, PartEventListType> {

	@Override
	protected PartDataAccessor create() {
		return new PartDataAccessor();
	}

	@Override
	protected PartEventListType createListType() {
		return objectFactory.createPartEventListType();
	}

	@Override
	protected PartEventType createXmlType() {
		return objectFactory.createPartEventType();
	}

	@Override
	protected void setId(PartEventType type, String id) {
		type.setPartId(id);
	}

	@Override
	protected void setUsage(PartEventType type, long usage) {
		type.setDuration(usage);
	}

}
