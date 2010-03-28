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

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

public class FileEventStorerTest
		extends AbstractContinuousEventStorerTest<FileEvent, FileEventType, FileEventListType> {

	@Override
	public void testHasSameId_typeAndEvent() {
		String id = "asdfsdf23";
		FileEventType type = new ObjectFactory().createFileEventType();
		type.setFileId(id);
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, id);
		Assert.assertTrue(storer.hasSameId(type, event));
	}

	@Override
	public void testHasSameId_typeAndType() {
		String id = "asdfsdf23";
		FileEventType type1 = new ObjectFactory().createFileEventType();
		type1.setFileId(id);
		FileEventType type2 = new ObjectFactory().createFileEventType();
		type2.setFileId(id);
		Assert.assertTrue(storer.hasSameId(type1, type2));
	}

	@Override
	protected FileEventStorer create() {
		return FileEventStorer.getInstance();
	}

	@Override
	protected FileEvent createEvent() {
		return new FileEvent(Calendar.getInstance(), 10, "someId");
	}

	@Override
	protected FileEvent createEvent2() {
		return new FileEvent(Calendar.getInstance(), 110, "blah");
	}

	@Override
	protected List<FileEventType> getEventTypes(FileEventListType type) {
		return type.getFileEvent();
	}

	@Override
	protected boolean isEqual(FileEventType type, FileEvent event) {
		boolean isEqual = type.getFileId().equals(event.getFileId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override
	protected FileEvent mergeValue(FileEvent main, FileEvent tmp) {
		return new FileEvent(main.getTime(), tmp.getDuration() + main.getDuration(), main.getFileId());
	}

	@Override
	protected FileEvent createEvent(Calendar eventTime) {
		return new FileEvent(eventTime, 10, "someIdabvc");
	}

}
