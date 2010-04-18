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

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.internal.xml.AbstractAccessor;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.IPath;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Test for {@link AbstractAccessor}
 */
@SuppressWarnings("restriction")
public abstract class AbstractAccessorTest<T, E, S extends EventGroupType> {

  private AbstractAccessor<T, E, S> accessor = create();

  protected ObjectFactory objectFactory = new ObjectFactory();

  private static final IPath originalRoot = XmlPlugin.getDefault()
      .getStoragePathRoot();

  @AfterClass
  public static void afterClass() {
    XmlPlugin.getDefault().setStoragePathRoot(originalRoot.toFile());
  }

  @BeforeClass
  public static void beforeClass() {
    // Set the directory root to a new folder so that we don't mess
    // with existing data:
    File dir = originalRoot.append(System.currentTimeMillis() + "").toFile();
    if (!dir.exists() && !dir.mkdirs()) {
      Assert.fail();
    }
    XmlPlugin.getDefault().setStoragePathRoot(dir);
  }

  @Test
  public void testGetCategories() throws Exception {
    EventListType eventList = objectFactory.createEventListType();
    S list = createCategory();
    getCategories(accessor, eventList).add(list);
    Assert.assertEquals(1, getCategories(accessor, eventList).size());
  }

  @Test(expected = NullPointerException.class)
  public void testGetData_endDateNull() {
    accessor.getData(new LocalDate(), null);
  }

  @Test
  public void testGetData_listsWithDifferentDates() throws Exception {
    String id = "qfnnvkfde877thfg";

    // 1:

    int count1 = 298;
    E type1 = createElement();
    setId(type1, id);
    setUsage(type1, count1);

    S list1 = createCategory();
    MutableDateTime tmp = new MutableDateTime();
    tmp.setDayOfMonth(1);
    XMLGregorianCalendar start = toXmlDate(tmp.toDateTime());
    list1.setDate(start);
    getElements(list1).add(type1);

    // 2:

    int count2 = 22817;
    E type2 = createElement();
    setId(type2, id);
    setUsage(type2, count2);

    S list2 = createCategory();
    tmp.setDayOfMonth(3);
    XMLGregorianCalendar end = toXmlDate(tmp.toDateTime());
    list2.setDate(end);
    getElements(list2).add(type2);

    EventListType events = objectFactory.createEventListType();
    getCategories(accessor, events).add(list1);
    getCategories(accessor, events).add(list2);

    File f = getDataStore(accessor).getDataFile(tmp.toDateTime().toLocalDate());
    getDataStore(accessor).write(events, f);

    assertValues(accessor.getData(new LocalDate(start.toGregorianCalendar()
        .getTimeInMillis()), new LocalDate(end.toGregorianCalendar()
        .getTimeInMillis())), events);
  }

  /**
   * Tests that two lists with the same date are stored, then getting the data
   * out should return the combined data. Note that although two lists with the
   * same date should not have happened.
   */
  @Test
  public void testGetData_listsWithSameDate() throws Exception {
    DateTime date = new DateTime();
    String id = "qfnnvkfde877thfg";

    // 1:

    int count1 = 1232948;
    E type1 = createElement();
    setId(type1, id);
    setUsage(type1, count1);

    S list1 = createCategory();
    XMLGregorianCalendar start = toXmlDate(date);
    list1.setDate(start);
    getElements(list1).add(type1);

    // 2:

    int count2 = 2342817;
    E type2 = createElement();
    setId(type2, id);
    setUsage(type2, count2);

    S list2 = createCategory();
    XMLGregorianCalendar end = toXmlDate(date);
    list2.setDate(end);
    getElements(list2).add(type2);

    EventListType events = objectFactory.createEventListType();
    getCategories(accessor, events).add(list1);
    getCategories(accessor, events).add(list2);

    File f = getDataStore(accessor).getDataFile(date.toLocalDate());
    getDataStore(accessor).write(events, f);

    assertValues(accessor.getData(new LocalDate(start.toGregorianCalendar()
        .getTimeInMillis()), new LocalDate(end.toGregorianCalendar()
        .getTimeInMillis())), events);
  }

  @Test(expected = NullPointerException.class)
  public void testGetData_startDateNull() {
    accessor.getData(null, new LocalDate());
  }

  @Test
  public void testGetDataStore() throws Exception {
    assertNotNull(getDataStore(accessor));
  }

  /**
   * Checks the values are OK against the two parameters, one is returned by the
   * IAccessor, one is the raw data.
   */
  protected abstract void assertValues(Collection<T> data, EventListType events)
      throws Exception;

  /** Creates a subject for testing. */
  protected abstract AbstractAccessor<T, E, S> create();

  /**
   * Creates a new list type.
   * 
   * @return A new list type.
   */
  protected abstract S createCategory();

  /**
   * Creates a new XML type with fields filled.
   * 
   * @return A new XML type.
   */
  protected abstract E createElement();

  /**
   * Calls the protected method {@code AbstractAccessor.filter(List<S>)}.
   */
  @SuppressWarnings("unchecked")
  protected T filter(AbstractAccessor accessor, List<S> list) throws Exception {
    Method method = AbstractAccessor.class.getDeclaredMethod("filter",
        List.class);
    method.setAccessible(true);
    return (T) method.invoke(accessor, list);
  }

  /**
   * Calls the protected method {@code
   * AbstractAccessor.getCategories(EventListType)}.
   */
  @SuppressWarnings("unchecked")
  protected Collection<S> getCategories(AbstractAccessor accessor,
      EventListType list) throws Exception {
    Method method = AbstractAccessor.class.getDeclaredMethod("getCategories",
        EventListType.class);
    method.setAccessible(true);
    return (Collection<S>) method.invoke(accessor, list);
  }

  /**
   * Calls the protected method {@code AbstractAccessor.getDataStore()}.
   */
  protected IDataStore getDataStore(AbstractAccessor<T, E, S> accessor)
      throws Exception {
    Method method = AbstractAccessor.class.getDeclaredMethod("getDataStore");
    method.setAccessible(true);
    return (IDataStore) method.invoke(accessor);
  }

  /**
   * Calls the protected method {@code AbstractAccessor.getDataStore()}.
   */
  @SuppressWarnings("unchecked")
  protected List<S> getXmlData(AbstractAccessor<T, E, S> accessor,
      Calendar start, Calendar end) throws Exception {
    Method method = AbstractAccessor.class.getDeclaredMethod("getXmlData",
        Calendar.class, Calendar.class);
    method.setAccessible(true);
    return (List<S>) method.invoke(accessor, start, end);
  }

  /**
   * Gets the list of XML event types.
   */
  protected abstract List<E> getElements(S list);

  /**
   * Sets the id of the type.
   * 
   * @param type The type.
   * @param id The id.
   */
  protected abstract void setId(E type, String id);

  /**
   * Sets the usage of the type.
   * 
   * @param type The type.
   * @param usage The usage.
   */
  protected abstract void setUsage(E type, long usage);
}