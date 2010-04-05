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
package rabbit.data.internal.xml;

import static rabbit.data.internal.xml.DatatypeUtil.isSameMonthInYear;

import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.DiscreteEvent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This abstract class is designed specifically for the XML schema. This class
 * contains implementations for common behaviors.
 * 
 * @param <E> The event type to be stored. Such as
 *          {@link rabbit.core.events.CommandEvent} .
 * @param <T> The corresponding XML object type of the event type, this is the
 *          form when the event is stored in XML.
 * @param <S> A {@link EventGroupType} that groups the XML types according to
 *          event date.
 */
public abstract class AbstractStorer<E extends DiscreteEvent, T, S extends EventGroupType>
    implements IStorer<E> {

  /** Factory object for creating XML schema Java types. */
  protected final ObjectFactory objectFactory = new ObjectFactory();

  /** Data in memory, not yet saved. */
  private Set<S> data;

  /** The current month. */
  private LocalDate currentMonth;

  /**
   * Constructor.
   */
  public AbstractStorer() {
    data = new LinkedHashSet<S>();
    currentMonth = new LocalDate();
  }

  @Override
  public void commit() {

    if (data.isEmpty()) {
      return;
    }

    File f = getDataStore().getDataFile(currentMonth);
    EventListType events = getDataStore().read(f);
    List<S> mainList = getXmlTypeCategories(events);

    for (S newList : data) {

      boolean done = false;
      for (S oldList : mainList) {
        if (newList.getDate().equals(oldList.getDate())) {
          merge(getXmlTypes(oldList), getXmlTypes(newList));
          done = true;
          break;
        }
      }

      if (!done) {
        mainList.add(newList);
      }
    }

    if (!getDataStore().write(events, f)) {
      XmlPlugin.getDefault().getLog()
          .log(
              new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID,
                  "Unable to save data."));
    }
    data.clear();
  }

  /**
   * Inserts a collection of event data to be stored.
   * 
   * @param col The collection of events.
   */
  @Override
  public void insert(Collection<? extends E> col) {

    for (E e : col) {
      insert(e);
    }
  }

  /**
   * Inserts an event to be stored.
   * 
   * @param e The event.
   */
  @Override
  public void insert(E e) {

    DateTime time = e.getTime();
    if (!isSameMonthInYear(e.getTime(), currentMonth)) {
      commit();
      currentMonth = time.toLocalDate();
    }

    boolean done = false;

    for (S list : data) {
      if (DatatypeUtil.isSameDate(e.getTime(), list.getDate())) {
        merge(getXmlTypes(list), e);
        done = true;
        break;
      }
    }

    if (!done) {
      S holder = newXmlTypeHolder(DatatypeUtil.toXMLGregorianCalendarDate(e
          .getTime()));
      merge(getXmlTypes(holder), e);
      data.add(holder);
    }
  }

  /**
   * Gets the data store.
   * 
   * @return The data store.
   */
  protected abstract IDataStore getDataStore();

  /**
   * Gets the XML categories for grouping the event objects by date in a
   * {@link EventListType}.
   * 
   * @param events The root element.
   * @return A list of groups.
   */
  protected abstract List<S> getXmlTypeCategories(EventListType events);

  /**
   * Gets the XML types from the given group.
   * 
   * @param list The group holding the XML types.
   * @return A list of XML types.
   */
  protected abstract List<T> getXmlTypes(S list);

  /**
   * Merges the event into the list.
   * 
   * @param xList The list for merging the event into.
   * @param event The event for merging.
   */
  protected abstract void merge(List<T> xList, E event);

  /**
   * Merges the second list into the first list.
   * 
   * @param mainList The list for merging into.
   * @param newList The list for getting data from.
   */
  protected abstract void merge(List<T> mainList, List<T> newList);

  /**
   * Creates a new XML object group type from the given date.
   * 
   * @param date The date.
   * @return A new XML object group type configured with the date.
   */
  protected abstract S newXmlTypeHolder(XMLGregorianCalendar date);
}
