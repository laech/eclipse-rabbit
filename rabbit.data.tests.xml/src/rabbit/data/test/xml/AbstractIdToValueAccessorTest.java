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
package rabbit.data.test.xml;

import rabbit.data.internal.xml.AbstractIdToValueAccessor;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @see AbstractIdToValueAccessor
 */
public abstract class AbstractIdToValueAccessorTest<E, S extends EventGroupType>
		extends AbstractAccessorTest<Map<String, Long>, E, S> {

	private AbstractIdToValueAccessor<E, S> accessor = create();

	@Test
	public void testGetId() throws Exception {
		String id = "2983jncjdkf";
		E type = createXmlType();
		setId(type, id);
		Assert.assertEquals(id, getId(accessor, type));
	}
//	getUsage(E)
//	getXmlTypes(S)\
	 /**
   * Calls the protected method {@code AbsractIdToValuegetUsage(accessor, E)}.
   */
  protected long getUsage(AbstractIdToValueAccessor<E, S> accessor, E e) throws Exception {
    Method method = AbstractIdToValueAccessor.class.getDeclaredMethod("getUsage", Object.class);
    method.setAccessible(true);
    return (Long) method.invoke(accessor, e);
  }
  /**
   * Calls the protected method {@code AbsractIdToValuegetXmlTypes(accessor, S)}.
   */
  @SuppressWarnings("unchecked")
  protected Collection<E> getXmlTypes(AbstractIdToValueAccessor accessor, S s) throws Exception {
    Method method = AbstractIdToValueAccessor.class.getDeclaredMethod("getXmlTypes", EventGroupType.class);
    method.setAccessible(true);
    return (Collection<E>) method.invoke(accessor, s);
  }
	/**
	 * Calls the protected method {@code AbsractIdToValuegetId(accessor, E)}.
	 */
	protected String getId(AbstractIdToValueAccessor<E, S> accessor, E e) throws Exception {
	  Method method = AbstractIdToValueAccessor.class.getDeclaredMethod("getId", Object.class);
	  method.setAccessible(true);
	  return (String) method.invoke(accessor, e);
	}
	
	@Test
	public void testGetUsage() throws Exception {
		long usage = 100193;
		E type = createXmlType();
		setUsage(type, usage);
		Assert.assertEquals(usage, getUsage(accessor, type));
	}

	@Test
	public void testGetXmlTypes() throws Exception {
		int size = 5;
		S list = createListType();
		for (int i = 0; i < size; i++) {
			getXmlTypes(accessor, list).add(createXmlType());
		}
		Assert.assertEquals(size, getXmlTypes(accessor, list).size());
	}

	@Override
	protected void assertValues(Map<String, Long> data, EventListType events) throws Exception {
		Map<String, Long> map = new HashMap<String, Long>();
		for (S list : getCategories(accessor, events)) {
			for (E e : getXmlTypes(accessor, list)) {
				Long usage = map.get(getId(accessor, e));
				if (usage == null) {
					map.put(getId(accessor, e), getUsage(accessor, e));
				} else {
					map.put(getId(accessor, e), getUsage(accessor, e) + usage);
				}
			}
		}

		assertEquals(map.size(), data.size());
		for (Entry<String, Long> entry : map.entrySet()) {
			assertEquals(entry.getValue(), data.get(entry.getKey()));
		}
	}

	@Override
	protected abstract AbstractIdToValueAccessor<E, S> create();
}