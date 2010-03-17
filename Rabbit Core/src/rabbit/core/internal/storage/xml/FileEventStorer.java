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

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

public class FileEventStorer extends
		AbstractStorer<FileEvent, FileEventType, FileEventListType> {

	@Override
	protected IDataStore getDataStore() {
		return DataStore.FILE_STORE;
	}

	@Override
	protected List<FileEventListType> getXmlTypeCategories(EventListType events) {
		return events.getFileEvents();
	}

	@Override
	protected boolean hasSameId(FileEventType x, FileEvent e) {
		boolean result = x.getFileId().equals(e.getFileId());
		if (result == false) {
			return result;
			
		} else if (x.getTaskHandleId() != null && e.getTask() != null) {
			result &= x.getTaskHandleId().equals(e.getTask().getHandleIdentifier());
			
		} else if (!(x.getTaskHandleId() == null && e.getTask() == null)) {
			result = false;
		}
		return result;
	}

	@Override
	protected boolean hasSameId(FileEventType x1, FileEventType x2) {
		boolean result = x1.getFileId().equals(x2.getFileId());
		if (result == false) {
			return result;
			
		} else if (x1.getTaskHandleId() != null && x2.getTaskHandleId() != null) {
			result &= x1.getTaskHandleId().equals(x2.getTaskHandleId());
			
		} else if (!(x1.getTaskHandleId() == null && x2.getTaskHandleId() == null)) {
			result = false;
		}
		return result;
	}

	@Override
	protected void merge(FileEventListType main, FileEvent e) {
		merge(main.getFileEvent(), e);
	}

	@Override
	protected void merge(FileEventListType main, FileEventListType data) {
		merge(main.getFileEvent(), data.getFileEvent());
	}

	@Override
	protected void merge(FileEventType main, FileEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(FileEventType main, FileEventType x) {
		main.setDuration(main.getDuration() + x.getDuration());
	}

	@Override
	protected FileEventType newXmlType(FileEvent e) {
		FileEventType type = OBJECT_FACTORY.createFileEventType();
		type.setDuration(e.getDuration());
		type.setFileId(e.getFileId());
		if (e.getTask() != null) {
			type.setTaskHandleId(e.getTask().getHandleIdentifier());
		}
		return type;
	}

	@Override
	protected FileEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		FileEventListType type = OBJECT_FACTORY.createFileEventListType();
		type.setDate(date);
		return type;
	}

}
