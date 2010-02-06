package rabbit.core.storage.xml;

import rabbit.core.internal.storage.xml.AbstractXmlAccessorTest;
import rabbit.core.internal.storage.xml.schema.events.CommandEventListType;
import rabbit.core.internal.storage.xml.schema.events.CommandEventType;
import rabbit.core.storage.xml.CommandDataAccessor;

/**
 * Test for {@link CommandDataAccessor}
 */
public class CommandDataAccessorTest extends AbstractXmlAccessorTest<CommandEventType, CommandEventListType> {

	@Override
	protected CommandDataAccessor create() {
		return new CommandDataAccessor();
	}

	@Override
	protected CommandEventListType createListType() {
		return objectFactory.createCommandEventListType();
	}

	@Override
	protected CommandEventType createXmlType() {
		return objectFactory.createCommandEventType();
	}

	@Override
	protected void setId(CommandEventType type, String id) {
		type.setCommandId(id);
	}

	@Override
	protected void setUsage(CommandEventType type, long usage) {
		type.setCount((int) usage);
	}

}
