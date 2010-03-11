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
package rabbit.core.storage.xml;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import rabbit.core.internal.storage.xml.AbstractAccessor;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

/**
 * For getting data on file events.
 */
public class FileDataAccessor extends AbstractAccessor<FileEventType, FileEventListType> {

	/**
	 * {@inheritDoc}
	 * 
	 * The keys of the map returned are file IDs.
	 */
	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		return super.getData(start, end);
	}

	@Override
	protected Collection<FileEventListType> getCategories(EventListType doc) {
		return doc.getFileEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

	@Override
	protected String getId(FileEventType e) {
		return e.getFileId();
	}

	@Override
	protected long getUsage(FileEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<FileEventType> getXmlTypes(FileEventListType list) {
		return list.getFileEvent();
	}

}
