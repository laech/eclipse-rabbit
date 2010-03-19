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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rabbit.core.internal.storage.xml.schema.events.EventGroupType;

/**
 * Represents a accessor that returns a map of data, where the keys of the map
 * are some kind of IDs, and the values of the map actual values for the keys.
 * 
 * @param <E>
 *            The XML event type.
 * @param <S>
 *            The container of the XML event types.
 */
public abstract class AbstractIdToValueAccessor<E, S extends EventGroupType>
		extends AbstractAccessor<Map<String, Long>, E, S> {

	@Override
	protected Map<String, Long> filter(List<S> data) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		for (S list : data) {
			for (E e : getXmlTypes(list)) {
				Long usage = result.get(getId(e));
				if (usage == null) {
					result.put(getId(e), getUsage(e));
				} else {
					result.put(getId(e), getUsage(e) + usage);
				}
			}
		}
		return result;
	}

	/**
	 * Gets the id of the given type.
	 * 
	 * @param e
	 *            The type.
	 * @return The id.
	 */
	protected abstract String getId(E e);

	/**
	 * Gets the usage info from the given type.
	 * 
	 * @param e
	 *            The type to get info from.
	 * @return The usage info.
	 */
	protected abstract long getUsage(E e);

	/**
	 * Gets a collection of types from the given category.
	 * 
	 * @param list
	 *            The category.
	 * @return A collection of objects.
	 */
	protected abstract Collection<E> getXmlTypes(S list);
}
