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

import rabbit.core.internal.storage.xml.AbstractIdToValueAccessorTest;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

/**
 * Test for {@link FileDataAccessor}
 */
public class FileDataAccessorTest extends
		AbstractIdToValueAccessorTest<FileEventType, FileEventListType> {

	@Override
	protected FileDataAccessor create() {
		return new FileDataAccessor();
	}

	@Override
	protected FileEventListType createListType() {
		return objectFactory.createFileEventListType();
	}

	@Override
	protected FileEventType createXmlType() {
		return objectFactory.createFileEventType();
	}

	@Override
	protected void setId(FileEventType type, String id) {
		type.setFileId(id);
	}

	@Override
	protected void setUsage(FileEventType type, long usage) {
		type.setDuration(usage);
	}

}
