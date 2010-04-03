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

import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.DurationEventType;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.store.model.ContinuousEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public abstract class AbstractContinuousEventStorerTest<E extends ContinuousEvent, T extends DurationEventType, S extends EventGroupType>
    extends AbstractDiscreteEventStorerTest<E, T, S> {

  @Override
  public void testCommit() {

    try {
      E e = createEvent();
      storer.insert(e);
      storer.commit();
      assertTrue(dataFile.exists());

      List<S> allEvents = getXmlTypeCategories(storer,
          getDataStore(storer).read(dataFile));
      assertEquals(1, allEvents.size());

      S list = allEvents.get(0);
      assertEquals(1, getEventTypes(list).size());

      T type = getEventTypes(list).get(0);
      assertTrue(isEqual(type, e));

      assertTrue(getDataField(storer).isEmpty());

      // ...

      storer.insert(e);
      storer.commit();

      allEvents = getXmlTypeCategories(storer, getDataStore(storer).read(
          dataFile));
      assertEquals(1, allEvents.size());

      list = allEvents.get(0);
      // The two events should have been merged because they are
      // from the same date, and has the same "id"
      assertEquals(1, getEventTypes(list).size());

      type = getEventTypes(list).get(0);
      e = mergeValue(e, e);
      assertTrue(isEqual(type, e));

      // ...

      // Insert an new and different event:

      E e2 = createEvent2();
      storer.insert(e2);
      storer.commit();

      allEvents = getXmlTypeCategories(storer, getDataStore(storer).read(
          dataFile));
      assertEquals(1, allEvents.size());

      list = allEvents.get(0);
      assertEquals(2, getEventTypes(list).size());

      T newType = getEventTypes(list).get(0);
      if (hasSameId(storer, type, newType)) {
        newType = getEventTypes(list).get(1);
        type = getEventTypes(list).get(0);
      } else {
        type = getEventTypes(list).get(0);
      }

      assertTrue(isEqual(newType, e2));
      assertTrue(isEqual(type, e));

      // ..

      Calendar cal = e.getTime();
      int day = cal.get(Calendar.DAY_OF_MONTH);
      day = (day < 15) ? day + 1 : day - 1;
      cal.set(Calendar.DAY_OF_MONTH, day);
      e = createEvent(cal);
      storer.insert(e);
      storer.commit();

      allEvents = getXmlTypeCategories(storer, getDataStore(storer).read(
          dataFile));
      assertEquals(2, allEvents.size());

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  protected abstract E createEvent(Calendar eventTime);

  @Override
  public void testInsert() {

    try {
      Collection<S> data = getDataField(storer);

      assertEquals(0, data.size());

      // Insert a new event:

      E e = createEvent();
      storer.insert(e);

      assertEquals(1, data.size());
      assertEquals(1, getEventTypes(data.iterator().next()).size());

      T type = getEventTypes(data.iterator().next()).get(0);
      assertTrue(isEqual(type, e));

      // Insert an event

      e = createEvent();
      storer.insert(e);

      assertEquals(1, data.size());
      assertEquals(1, getEventTypes(data.iterator().next()).size());

      type = getEventTypes(data.iterator().next()).get(0);
      e = mergeValue(e, e);
      assertTrue(isEqual(type, e));

      // Insert an new and different event:

      e = createEvent2();
      storer.insert(e);

      assertEquals(1, data.size());
      assertEquals(2, getEventTypes(data.iterator().next()).size());

      type = getEventTypes(data.iterator().next()).get(1);
      assertTrue(isEqual(type, e));

      Calendar cal = e.getTime();
      int day = cal.get(Calendar.DAY_OF_MONTH);
      day = (day < 15) ? day + 1 : day - 1;
      cal.set(Calendar.DAY_OF_MONTH, day);
      e = createEvent(cal);

      storer.insert(e);

      assertEquals(2, data.size());

    } catch (Exception ex) {
      ex.printStackTrace();
      fail();
    }
  }

  @Override
  public void testInsertCollection() {
    try {
      Collection<S> data = getDataField(storer);

      assertEquals(0, data.size());

      E e = null;
      T type = null;
      List<E> list = new ArrayList<E>();

      {// Insert a new event:
        e = createEvent();
        list.add(e);
        storer.insert(list);

        assertEquals(1, data.size());

        assertEquals(1, getEventTypes(data.iterator().next()).size());

        type = getEventTypes(data.iterator().next()).iterator().next();

        assertTrue(isEqual(type, e));
      }

      {// Insert collection with two elements:

        // Make a new event with the same id to be merged:
        E eWithSameId = createEvent();

        // Make a new event with a different id:
        E eNew = createEvent2();

        list.clear();
        list.add(eWithSameId);
        list.add(eNew);
        storer.insert(list);

        assertEquals(1, data.size());
        assertEquals(2, getEventTypes(data.iterator().next()).size());

        type = getEventTypes(data.iterator().next()).get(0);
        eWithSameId = mergeValue(eWithSameId, e);
        assertTrue(isEqual(type, eWithSameId));

        type = getEventTypes(data.iterator().next()).get(1);
        assertTrue(isEqual(type, eNew));
      }

      {// Insert event of a different date:
        list.clear();
        Calendar cal = e.getTime();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        day = (day < 15) ? day + 1 : day - 1;
        cal.set(Calendar.DAY_OF_MONTH, day);
        e = createEvent(cal);

        list.add(e);
        storer.insert(list);

        assertEquals(2, data.size());
      }

    } catch (Exception ex) {
      ex.printStackTrace();
      fail();
    }
  }

  @Override
  public void testMerge_typeAndType() throws Exception {

    E e = createEvent();
    T main = newXmlType(storer, e);
    T tmp = newXmlType(storer, e);

    long totalDuration = main.getDuration() + tmp.getDuration();
    merge(storer, main, tmp);
    assertEquals(totalDuration, main.getDuration());
  }

  @Override
  public void testNewXmlTypeHolder() throws Exception {

    XMLGregorianCalendar cal = getCalendar();
    S type = newXmlTypeHolder(storer, cal);
    assertEquals(cal, type.getDate());
  }

  @Override
  public void testNewXmlType() throws Exception {

    E e = createEvent();
    T xml = newXmlType(storer, e);

    assertTrue(hasSameId(xml, e));
    assertEquals(xml.getDuration(), e.getDuration());
  }

  /**
   * Checks whether the two has the same ID.
   */
  protected abstract boolean hasSameId(T xml, E e);

  /** Gets the list of events form the parameter. */
  protected abstract List<T> getEventTypes(S type);

  @Override
  public void testMerge_listOfXmlTypesAndEvent() throws Exception {
    List<T> list = new ArrayList<T>();
    E event = createEvent();

    merge(storer, list, event);
    assertEquals(1, list.size());
    T type = list.get(0);
    assertTrue(hasSameId(type, event));
    assertEquals(type.getDuration(), event.getDuration());

    // Repeat, two object in the list should be merged:
    merge(storer, list, event);
    assertEquals(1, list.size());
    type = list.get(0);
    assertTrue(hasSameId(type, event));
    // We merged twice, so the duration should have been doubled.
    assertEquals(type.getDuration(), event.getDuration() * 2);
  }

  @Override
  public void testMerge_listOfXmlTypesAndListOfXmlTypes() throws Exception {
    List<T> list1 = new ArrayList<T>();
    List<T> list2 = new ArrayList<T>();
    E event = createEvent();
    T type = newXmlType(storer, event);
    list2.add(type);

    merge(storer, list1, list2);
    assertEquals(1, list1.size());
    assertTrue(hasSameId(list1.get(0), event));
    assertEquals(event.getDuration(), list1.get(0).getDuration());

    // Repeat, two object in the list should be merged:
    merge(storer, list1, list2);
    assertEquals(1, list1.size());
    assertTrue(hasSameId(list1.get(0), event));
    // We merged twice, so the duration should have been doubled.
    assertEquals(list1.get(0).getDuration(), event.getDuration() * 2);
  }

  /** Returns true if the parameters contains the same values. */
  protected abstract boolean isEqual(T type, E event);

  /**
   * Returns a new element who's attributes are base on the first parameter, and
   * the value is the combination of the two elements.
   */
  protected abstract E mergeValue(E main, E tmp);

  private XMLGregorianCalendar getCalendar() {
    return DatatypeUtil.toXMLGregorianCalendarDate(Calendar.getInstance());
  }

}
