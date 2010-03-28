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

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.CommandEvent;
import rabbit.core.internal.storage.xml.schema.events.CommandEventListType;
import rabbit.core.internal.storage.xml.schema.events.CommandEventType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;

public final class CommandEventStorer
		extends AbstractDiscreteEventStorer<CommandEvent, CommandEventType, CommandEventListType> {

	private static final CommandEventStorer INSTANCE = new CommandEventStorer();

	/**
	 * Gets the shared instance of this class.
	 * 
	 * @return The shared instance of this class.
	 */
	public static CommandEventStorer getInstance() {
		return INSTANCE;
	}

	private CommandEventStorer() {
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.COMMAND_STORE;
	}

	@Override
	protected List<CommandEventListType> getXmlTypeCategories(EventListType events) {
		return events.getCommandEvents();
	}

	@Override
	protected List<CommandEventType> getXmlTypes(CommandEventListType list) {
		return list.getCommandEvent();
	}

	@Override
	protected boolean hasSameId(CommandEventType x, CommandEvent e) {

		return e.getExecutionEvent().getCommand().getId()
				.equals(x.getCommandId());
	}

	@Override
	protected boolean hasSameId(CommandEventType x1, CommandEventType x2) {
		return x1.getCommandId().equals(x2.getCommandId());
	}

	@Override
	protected void merge(CommandEventType x, CommandEvent e) {
		x.setCount(x.getCount() + 1);
	}

	@Override
	protected void merge(CommandEventType x1, CommandEventType x2) {
		x1.setCount(x1.getCount() + x2.getCount());
	}

	@Override
	protected CommandEventType newXmlType(CommandEvent e) {
		CommandEventType type = objectFactory.createCommandEventType();
		type.setCommandId(e.getExecutionEvent().getCommand().getId());
		type.setCount(1);
		return type;
	}
	
	@Override
	protected CommandEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		CommandEventListType type = objectFactory.createCommandEventListType();
		type.setDate(date);
		return type;
	}
}
