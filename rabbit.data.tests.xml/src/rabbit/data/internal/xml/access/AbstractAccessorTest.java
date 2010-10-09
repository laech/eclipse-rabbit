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
package rabbit.data.internal.xml.access;

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.IPath;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Test for {@link AbstractAccessor}
 */
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
    accessor.getCategories(eventList).add(list);
    Assert.assertEquals(1, accessor.getCategories(eventList).size());
  }

  @Test(expected = NullPointerException.class)
  public void testGetData_endDateNull() {
    accessor.getData(new LocalDate(), null);
  }

  @Test
  public void testGetData() throws Exception {
    String id = "qfnnvkfde877thfg";

    // 1:

    int count1 = 298;
    E type1 = createElement();
    setId(type1, id);
    setValue(type1, count1);

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
    setValue(type2, count2);

    S list2 = createCategory();
    tmp.setDayOfMonth(3);
    XMLGregorianCalendar end = toXmlDate(tmp.toDateTime());
    list2.setDate(end);
    getElements(list2).add(type2);

    EventListType events = objectFactory.createEventListType();
    accessor.getCategories(events).add(list1);
    accessor.getCategories(events).add(list2);

    File f = accessor.getDataStore().getDataFile(tmp.toDateTime().toLocalDate());
    accessor.getDataStore().write(events, f);

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
    assertNotNull(accessor.getDataStore());
  }

  /**
   * Checks the values are OK against the two parameters, one is returned by the
   * IAccessor, one is the raw data.
   */
  protected abstract void assertValues(
      Collection<T> actual, EventListType events) throws Exception;

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
  protected abstract void setValue(E type, long usage);
}
