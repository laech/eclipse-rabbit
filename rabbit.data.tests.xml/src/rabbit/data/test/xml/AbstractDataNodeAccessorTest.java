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

import static rabbit.data.internal.xml.DatatypeUtil.toLocalDate;

import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @see AbstractDataNodeAccessor
 */
@SuppressWarnings("restriction")
public abstract class AbstractDataNodeAccessorTest<T, E, S extends EventGroupType>
    extends AbstractAccessorTest2<T, E, S> {

  protected AbstractDataNodeAccessor<T, E, S> accessor = create();

  @Test
  public abstract void testCreateDataNode() throws Exception;

  @Test
  public void testGetXmlTypes() throws Exception {
    int size = 5;
    S list = createListType();
    for (int i = 0; i < size; i++) {
      getXmlTypes(accessor, list).add(createXmlType());
    }
    assertEquals(size, getXmlTypes(accessor, list).size());
  }

  @Override
  protected abstract AbstractDataNodeAccessor<T, E, S> create();

  /**
   * Calls the protected method {@code AbstractDataNodeAccessor.createDataNode}
   */
  @SuppressWarnings("unchecked")
  protected T createDataNode(AbstractDataNodeAccessor<T, E, S> accessor,
      LocalDate date, E e) throws Exception {

    Method method = AbstractDataNodeAccessor.class.getDeclaredMethod(
        "createDataNode", LocalDate.class, Object.class);
    method.setAccessible(true);
    return (T) method.invoke(accessor, date, e);
  }

  /**
   * This method in this class assumes all T have equals(Object) method
   * overridden.
   */
  @Override
  protected void assertValues(Collection<T> data, EventListType events)
      throws Exception {

    List<T> checkList = new LinkedList<T>();
    for (S list : getCategories(accessor, events)) {
      for (E type : getXmlTypes(list)) {
        checkList.add(createDataNode(accessor, toLocalDate(list.getDate()),
            type));
      }
    }

    assertEquals(checkList.size(), data.size());
    assertTrue(data.containsAll(checkList));
  }

  /**
   * Calls the protected method {@code AbstractDataNodeAccessor.getXmlTypes(S)}
   */
  @SuppressWarnings("unchecked")
  protected Collection<E> getXmlTypes(
      AbstractDataNodeAccessor<T, E, S> accessor, S s) throws Exception {

    Method method = AbstractDataNodeAccessor.class.getDeclaredMethod(
        "getXmlTypes", EventGroupType.class);
    method.setAccessible(true);
    return (Collection<E>) method.invoke(accessor, s);
  }
}
