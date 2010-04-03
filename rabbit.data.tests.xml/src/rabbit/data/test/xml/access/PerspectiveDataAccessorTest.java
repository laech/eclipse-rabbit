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

import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.test.xml.AbstractIdToValueAccessorTest;
import rabbit.data.xml.access.PerspectiveDataAccessor;

import java.util.List;

public class PerspectiveDataAccessorTest extends
		AbstractIdToValueAccessorTest<PerspectiveEventType, PerspectiveEventListType> {

	@Override
	protected PerspectiveDataAccessor create() {
		return new PerspectiveDataAccessor();
	}

	@Override
	protected PerspectiveEventListType createListType() {
		return objectFactory.createPerspectiveEventListType();
	}

	@Override
	protected PerspectiveEventType createXmlType() {
		return objectFactory.createPerspectiveEventType();
	}

	@Override
	protected void setId(PerspectiveEventType type, String id) {
		type.setPerspectiveId(id);
	}

	@Override
	protected void setUsage(PerspectiveEventType type, long usage) {
		type.setDuration(usage);
	}

	@Override
	protected List<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
		return list.getPerspectiveEvent();
	}

}
