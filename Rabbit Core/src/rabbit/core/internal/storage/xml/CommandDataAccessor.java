/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.storage.xml;

import java.util.Collection;

import rabbit.core.internal.storage.xml.schema.events.CommandEventListType;
import rabbit.core.internal.storage.xml.schema.events.CommandEventType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;

public class CommandDataAccessor extends AbstractIdToValueAccessor<CommandEventType, CommandEventListType> {

	@Override
	protected Collection<CommandEventListType> getCategories(EventListType doc) {
		return doc.getCommandEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.COMMAND_STORE;
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

}
