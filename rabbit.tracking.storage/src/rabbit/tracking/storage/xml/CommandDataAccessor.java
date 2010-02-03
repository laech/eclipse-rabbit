package rabbit.tracking.storage.xml;

import java.util.Collection;

import rabbit.tracking.storage.xml.schema.CommandEventListType;
import rabbit.tracking.storage.xml.schema.CommandEventType;
import rabbit.tracking.storage.xml.schema.EventListType;

public class CommandDataAccessor extends AbstractXmlAccessor<CommandEventType, CommandEventListType> {

	@Override
	protected Collection<CommandEventListType> getCategories(EventListType doc) {
		return doc.getCommandEvents();
	}

	@Override
	protected String getId(CommandEventType e) {
		return e.getCommandId();
	}

	@Override
	protected long getUsage(CommandEventType e) {
		return e.getCount();
	}

	@Override
	protected Collection<CommandEventType> getXmlTypes(CommandEventListType list) {
		return list.getCommandEvent();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.COMMAND_STORE;
	}

}
