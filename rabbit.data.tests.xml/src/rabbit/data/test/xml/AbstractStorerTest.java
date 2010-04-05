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

import static rabbit.data.internal.xml.DatatypeUtil.toXMLGregorianCalendarDate;

import rabbit.data.internal.xml.AbstractStorer;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.store.model.DiscreteEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see AbstractStorer
 */
public abstract class AbstractStorerTest<E extends DiscreteEvent, T, S extends EventGroupType> {

  private AbstractStorer<E, T, S> storer = create();

  protected ObjectFactory objectFactory = new ObjectFactory();

  @Test
  public abstract void testCommit() throws Exception;

  @Test
  public void testGetDataStore() throws Exception {
    assertNotNull(getDataStore(storer));
  }

  @Test
  public void testGetXmlTypeCategories() throws Exception {
    assertNotNull(getXmlTypeCategories(storer, objectFactory
        .createEventListType()));
  }

  @Test
  public abstract void testInsert() throws Exception;

  @Test
  public abstract void testInsertCollection() throws Exception;

  /**
   * @see AbstractStorer#merge(java.util.List, DiscreteEvent)
   */
  @Test
  public abstract void testMerge_listOfXmlTypesAndEvent() throws Exception;

  /**
   * @see AbstractStorer#merge(java.util.List, java.util.List)
   */
  @Test
  public abstract void testMerge_listOfXmlTypesAndListOfXmlTypes()
      throws Exception;

  @Test
  public void testNewXmlTypeHolder() throws Exception {
    XMLGregorianCalendar cal = toXMLGregorianCalendarDate(new DateTime());
    S s = newXmlTypeHolder(storer, cal);
    assertNotNull(s);
    assertEquals(cal, s.getDate());
  }

  protected abstract AbstractStorer<E, T, S> create();

  @SuppressWarnings("unchecked")
  protected Collection<S> getDataField(AbstractStorer<E, T, S> s)
      throws Exception {
    Field f = AbstractStorer.class.getDeclaredField("data");
    f.setAccessible(true);
    return (Collection<S>) f.get(s);
  }

  /**
   * Calls the protected method {@code AbstractStorer.getDataStore()}.
   */
  protected IDataStore getDataStore(AbstractStorer<E, T, S> storer)
      throws Exception {
    Method method = AbstractStorer.class.getDeclaredMethod("getDataStore");
    method.setAccessible(true);
    return (IDataStore) method.invoke(storer);
  }

  /**
   * Calls the protected method {@code
   * AbstractStorer.getXmlTypeCategories(EventListType)}.
   */
  @SuppressWarnings("unchecked")
  protected List<S> getXmlTypeCategories(AbstractStorer<E, T, S> storer,
      EventListType events) throws Exception {

    Method method = AbstractStorer.class.getDeclaredMethod(
        "getXmlTypeCategories", EventListType.class);
    method.setAccessible(true);
    return (List<S>) method.invoke(storer, events);
  }

  /**
   * Calls the protected method {@code AbstractStorer.getXmlTypes(S)}.
   */
  @SuppressWarnings("unchecked")
  protected List<T> getXmlTypes(AbstractStorer<E, T, S> storer, S events)
      throws Exception {

    Method method = AbstractStorer.class.getDeclaredMethod(
        "getXmlTypeCategories", EventListType.class);
    method.setAccessible(true);
    return (List<T>) method.invoke(storer, events);
  }

  /**
   * Calls the protected method {@code AbstractStorer.merge(List, E)}.
   */
  protected void merge(AbstractStorer<E, T, S> storer, List<T> list, E event)
      throws Exception {

    Method method = AbstractStorer.class.getDeclaredMethod("merge", List.class,
        DiscreteEvent.class);
    method.setAccessible(true);
    method.invoke(storer, list, event);
  }

  /**
   * Calls the protected method {@code AbstractStorer.merge(List, List)}.
   */
  protected void merge(AbstractStorer<E, T, S> storer, List<T> list,
      List<T> list2) throws Exception {

    Method method = AbstractStorer.class.getDeclaredMethod("merge", List.class,
        List.class);
    method.setAccessible(true);
    method.invoke(storer, list, list2);
  }

  /**
   * Calls the protected method {@code AbstractStorer.newXmlTypeHolder(Calendar}
   * .
   */
  @SuppressWarnings("unchecked")
  protected S newXmlTypeHolder(AbstractStorer storer, XMLGregorianCalendar cal)
      throws Exception {
    Method method = AbstractStorer.class.getDeclaredMethod("newXmlTypeHolder",
        XMLGregorianCalendar.class);
    method.setAccessible(true);
    return (S) method.invoke(storer, cal);
  }
}
