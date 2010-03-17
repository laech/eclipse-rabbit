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
import java.util.HashMap;
import java.util.Map;

import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;
import rabbit.core.storage.IAccessor;

/**
 * Gets the data about time spent on tasks and files.
 * <p>
 * The returned map's keys are task handle identifiers, the values are also
 * maps, the keys of the second maps are file IDs, and the values of the second
 * maps are durations in milliseconds.
 * </p>
 * 
 * @since 1.1
 */
public class TaskFileDataAccessor
		implements IAccessor<Map<String, Map<String, Long>>> {

	/*
	 * A helper class that does the real work.
	 */
	private class HelperAccessor extends FileDataAccessor {

		/*
		 * Gets the file event data out of the files, then keeps the ones that
		 * has tasks, then structures the data to the appropriate return type.
		 */
		private Map<String, Map<String, Long>> getTaskData(Calendar start, Calendar end) {
			Map<String, Map<String, Long>> data =
					new HashMap<String, Map<String, Long>>();

			for (FileEventListType list : getXmlData(start, end)) {
				for (FileEventType type : list.getFileEvent()) {
					if (type.getTaskHandleId() == null) {
						continue;
					}

					Map<String, Long> fileValues = data.get(type.getTaskHandleId());
					if (fileValues == null) {
						fileValues = new HashMap<String, Long>();
						data.put(type.getTaskHandleId(), fileValues);
					}

					Long duration = fileValues.get(type.getFileId());
					if (duration == null) {
						duration = Long.valueOf(0);
					}
					fileValues.put(type.getFileId(), type.getDuration() + duration);
				}
			}
			return data;
		}
	}

	private final HelperAccessor helper;

	/**
	 * Constructor.
	 */
	public TaskFileDataAccessor() {
		helper = new HelperAccessor();
	}

	@Override
	public Map<String, Map<String, Long>> getData(Calendar start, Calendar end) {
		return helper.getTaskData(start, end);
	}

}
