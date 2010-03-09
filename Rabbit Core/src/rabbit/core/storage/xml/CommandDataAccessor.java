package rabbit.core.storage.xml;

import java.util.Collection;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.CommandEventListType;
import rabbit.core.internal.storage.xml.schema.events.CommandEventType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;

public class CommandDataAccessor extends AbstractAccessor<CommandEventType, CommandEventListType> {

	@Override
	public Collection<CommandEventListType> getCategories(EventListType doc) {
		return doc.getCommandEvents();
	}

	@Override
	public String getId(CommandEventType e) {
		return e.getCommandId();
	}

	@Override
	public long getUsage(CommandEventType e) {
		return e.getCount();
	}

	@Override
	public Collection<CommandEventType> getXmlTypes(CommandEventListType list) {
		return list.getCommandEvent();
	}

	@Override
	public IDataStore getDataStore() {
		return DataStore.COMMAND_STORE;
	}

}
