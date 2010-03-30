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

import static rabbit.core.internal.storage.xml.DatatypeUtil.toXMLGregorianCalendarDateTime;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.CoreException;

import rabbit.core.events.LaunchEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;

/**
 * Stores {@link LaunchEvent}
 */
public class LaunchEventStorer extends
		AbstractStorer<LaunchEvent, LaunchEventType, LaunchEventListType> {

	private static LaunchEventStorer INSTANCE = new LaunchEventStorer();

	/**
	 * Gets the shared instance of this class.
	 * 
	 * @return The shared instance.
	 */
	public static LaunchEventStorer getInstance() {
		return INSTANCE;
	}

	private LaunchEventStorer() {
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.LAUNCH_STORE;
	}

	@Override
	protected List<LaunchEventListType> getXmlTypeCategories(EventListType events) {
		return events.getLaunchEvents();
	}

	@Override
	protected List<LaunchEventType> getXmlTypes(LaunchEventListType list) {
		return list.getLaunchEvent();
	}

	/**
	 * Creates a {@linkplain LaunchEventType} from the given event.
	 * 
	 * @param event
	 *            The event to get data from.
	 * @return A {@linkplain LaunchEventType} with the data copied from the
	 *         parameter.
	 */
	protected LaunchEventType newXmlType(LaunchEvent event) {
		LaunchEventType type = objectFactory.createLaunchEventType();
		type.getFileId().addAll(event.getFileIds());
		type.setDuration(event.getDuration());
		type.setLaunchName(event.getLaunchConfiguration().getName());
		type.setLaunchTime(toXMLGregorianCalendarDateTime((GregorianCalendar) event.getTime()));
		try {
			type.setLaunchTypeId(event.getLaunchConfiguration().getType().getIdentifier());
		} catch (CoreException ex) {
			ex.printStackTrace();
			type.setLaunchTypeId(null);
		}
		type.setLaunchModeId(event.getLaunch().getLaunchMode());

		return type;
	}

	@Override
	protected LaunchEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		LaunchEventListType type = objectFactory.createLaunchEventListType();
		type.setDate(date);
		return type;
	}

	@Override
	protected void merge(List<LaunchEventType> xList, LaunchEvent event) {
		xList.add(newXmlType(event));
	}

	@Override
	protected void merge(List<LaunchEventType> mainList, List<LaunchEventType> newList) {
		mainList.addAll(newList);
	}
}
