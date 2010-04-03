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
package rabbit.data.test.xml.access;

import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.test.xml.AbstractIdToValueAccessorTest;
import rabbit.data.xml.access.PartDataAccessor;

import java.util.List;

/**
 * Test for {@link PartDataAccessor}
 */
public class PartDataAccessorTest extends
		AbstractIdToValueAccessorTest<PartEventType, PartEventListType> {

	@Override
	protected PartDataAccessor create() {
		return new PartDataAccessor();
	}

	@Override
	protected PartEventListType createListType() {
		return objectFactory.createPartEventListType();
	}

	@Override
	protected PartEventType createXmlType() {
		return objectFactory.createPartEventType();
	}

	@Override
	protected void setId(PartEventType type, String id) {
		type.setPartId(id);
	}

	@Override
	protected void setUsage(PartEventType type, long usage) {
		type.setDuration(usage);
	}

	@Override
	protected List<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}

}
