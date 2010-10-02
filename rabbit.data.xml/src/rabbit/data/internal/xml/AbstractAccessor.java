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

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.IAccessor;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Abstract class provides default behaviors, this class is designed
 * specifically for the schema.
 * 
 * @param <T> The result type.
 * @param <E> The XML type.
 * @param <S> The XML category type.
 */
public abstract class AbstractAccessor<T, E, S extends EventGroupType>
    implements IAccessor<T> {

  private final IDataStore store;

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If any arguments are null.
   */
  protected AbstractAccessor(IDataStore store) {
    this.store = checkNotNull(store);
  }

  @Override
  public ImmutableCollection<T> getData(LocalDate start, LocalDate end) {
    if (start == null || end == null) {
      throw new NullPointerException();
    }
    return filter(getXmlData(start, end));
  }

  /**
   * Filters the given data.
   * 
   * @param data The raw data between the two dates of
   *          {@link #getData(Calendar, Calendar)}.
   * @return The filtered data.
   */
  protected abstract ImmutableCollection<T> filter(List<S> data);

  /**
   * Gets the collection of categories from the given parameter.
   * 
   * @param doc The root of a document type.
   * @return A collection of categories.
   */
  protected abstract Collection<S> getCategories(EventListType doc);

  /**
   * Gets the data store.
   * 
   * @return The data store.
   */
  protected final IDataStore getDataStore() {
    return store;
  }

  /**
   * Gets the data from the XML files.
   * 
   * @param start The start date of the data to get.
   * @param end The end date of the data to get.
   * @return The data between the dates, inclusive.
   */
  protected List<S> getXmlData(LocalDate start, LocalDate end) {
    List<S> data = new LinkedList<S>();
    XMLGregorianCalendar startXmlCal = toXmlDate(start);
    XMLGregorianCalendar endXmlCal = toXmlDate(end);

    List<File> files = getDataStore().getDataFiles(start, end);
    for (File f : files) {
      for (S list : getCategories(getDataStore().read(f))) {

        XMLGregorianCalendar date = list.getDate();
        if (date == null)
          continue;

        if (date.compare(startXmlCal) >= 0
            && list.getDate().compare(endXmlCal) <= 0) {

          data.add(list);
        }
      }
    }
    return data;
  }

}
