package rabbit.tracking.storage.xml;

import rabbit.tracking.storage.xml.CommandDataAccessor;
import rabbit.tracking.storage.xml.schema.CommandEventListType;
import rabbit.tracking.storage.xml.schema.CommandEventType;

/**
 * Test for {@link CommandDataAccessor}
 */
public class CommandDataAccessorTest extends AbstractXmlAccessorTest<CommandEventType, CommandEventListType> {

	@Override protected CommandDataAccessor create() {
		return new CommandDataAccessor();
	}

	@Override protected CommandEventListType createListType() {
		return objectFactory.createCommandEventListType();
	}

	@Override protected CommandEventType createXmlType() {
		return objectFactory.createCommandEventType();
	}

	@Override protected void setId(CommandEventType type, String id) {
		type.setCommandId(id);
	}

	@Override protected void setUsage(CommandEventType type, long usage) {
		type.setCount((int) usage);
	}

}
