/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.data.test.xml;

import static rabbit.data.internal.xml.DatatypeUtil.toLocalDate;

import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.Mergers;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see AbstractDataNodeAccessor
 */
@SuppressWarnings("restriction")
public abstract class AbstractDataNodeAccessorTest<E, T, S extends EventGroupType>
    extends AbstractAccessorTest<E, T, S> {

  protected AbstractDataNodeAccessor<E, T, S> accessor = create();

  @Test
  public abstract void testCreateDataNode() throws Exception;

  @Test
  public void testGetElements() throws Exception {
    int size = 5;
    S list = createCategory();
    for (int i = 0; i < size; i++) {
      getElements(accessor, list).add(createElement());
    }
    assertEquals(size, getElements(accessor, list).size());
  }

  @Override
  protected abstract AbstractDataNodeAccessor<E, T, S> create();

  /**
   * Calls the protected method {@code AbstractDataNodeAccessor.createDataNode}
   */
  @SuppressWarnings("unchecked")
  protected E createDataNode(AbstractDataNodeAccessor<E, T, S> accessor,
      LocalDate date, T e) throws Exception {

    Method method = AbstractDataNodeAccessor.class.getDeclaredMethod(
        "createDataNode", LocalDate.class, Object.class);
    method.setAccessible(true);
    return (E) method.invoke(accessor, date, e);
  }

  /**
   * This method in this class assumes all T have equals(Object) method
   * overridden.
   */
  @Override
  protected void assertValues(Collection<E> data, EventListType events)
      throws Exception {

    IMerger<T> merger = createMerger(accessor);
    SetMultimap<XMLGregorianCalendar, T> map = HashMultimap.create();
    for (S category : getCategories(accessor, events)) {
      if (merger != null)
        Mergers.merge(merger, map.get(category.getDate()),
            getElements(category));
      else
        map.get(category.getDate()).addAll(getElements(category));
    }

    Set<E> result = Sets.newHashSet();
    for (Entry<XMLGregorianCalendar, Collection<T>> entry : map.asMap()
        .entrySet()) {
      LocalDate date = toLocalDate(entry.getKey());
      for (T type : entry.getValue()) {
        E element = createDataNode(accessor, date, type);
        if (element != null)
          result.add(element);
      }
    }

    assertEquals(result.size(), data.size());
    assertTrue(result.containsAll(data));
    assertTrue(data.containsAll(result));
  }

  /**
   * Calls the protected method {@code AbstractDataNodeAccessor.getElements(S)}
   */
  @SuppressWarnings("unchecked")
  protected Collection<T> getElements(
      AbstractDataNodeAccessor<E, T, S> accessor, S s) throws Exception {

    Method method = AbstractDataNodeAccessor.class.getDeclaredMethod(
        "getElements", EventGroupType.class);
    method.setAccessible(true);
    return (Collection<T>) method.invoke(accessor, s);
  }

  /**
   * Calls the protected method {@code AbstractDataNodeAccessor.createMerger()};
   */
  @SuppressWarnings("unchecked")
  protected IMerger<T> createMerger(AbstractDataNodeAccessor<E, T, S> accessor)
      throws Exception {

    Method method = AbstractDataNodeAccessor.class
        .getDeclaredMethod("createMerger");
    method.setAccessible(true);
    return (IMerger<T>) method.invoke(accessor);
  }
}
