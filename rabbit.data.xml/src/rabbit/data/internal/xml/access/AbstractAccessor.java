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

import static rabbit.data.internal.xml.DatatypeUtil.toLocalDate;
import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.eclipse.core.runtime.IPath;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
  public final Collection<T> getData(LocalDate start, LocalDate end) {
    return filter(getXmlData(checkNotNull(start, "start date is null"),
        checkNotNull(end, "end date is null")));
  }

  /**
   * Creates a data node.
   * 
   * @param cal The date of the XML type.
   * @param type The XML type.
   * @return A data node, or null if one cannot be created.
   * @throws Exception If a data node cannot be created.
   */
  protected abstract T createDataNode(LocalDate cal, WorkspaceStorage ws, E type)
      throws Exception;

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
   * Gets a collection of types from the given category.
   * 
   * @param category The category.
   * @return A collection of objects.
   */
  protected abstract Collection<E> getElements(S category);

  /**
   * Filters the given data.
   * 
   * @param data The raw data between the two dates of
   *        {@link #getData(LocalDate, LocalDate)}.
   * @return The filtered data.
   */
  private Collection<T> filter(Multimap<WorkspaceStorage, S> data) {
    if (data.isEmpty()) {
      return Collections.emptyList();
    }

    // Find the number of elements to convert
    int capacity = 0;
    for (Entry<WorkspaceStorage, S> entry : data.entries()) {
      capacity += getElements(entry.getValue()).size();
    }
    if (capacity <= 0) {
      return Collections.emptyList();
    }

    final List<T> result = Lists.newArrayListWithCapacity(capacity);
    for (Entry<WorkspaceStorage, S> entry : data.entries()) {
      final S category = entry.getValue();
      final XMLGregorianCalendar xmlDate = category.getDate();
      final Collection<E> elements = getElements(category);
      if (elements.isEmpty() || xmlDate == null) {
        continue;
      }

      final LocalDate date = toLocalDate(xmlDate);
      for (E element : elements) {
        T node = null;
        try {
          node = createDataNode(date, entry.getKey(), element);
        } catch (Exception e) {
          node = null;
        }
        if (node != null) {
          result.add(node);
        }
      }
    }
    return result;
  }

  /**
   * Gets the data from the XML files.
   * 
   * @param start The start date of the data to get.
   * @param end The end date of the data to get.
   * @return The data between the dates, inclusive.
   */
  private Multimap<WorkspaceStorage, S> getXmlData(LocalDate start,
      LocalDate end) {

    final XMLGregorianCalendar startDate = toXmlDate(start);
    final XMLGregorianCalendar endDate = toXmlDate(end);
    final XmlPlugin plugin = XmlPlugin.getDefault();

    final IPath[] storagePaths = plugin.getStoragePaths();
    final Multimap<WorkspaceStorage, S> data = ArrayListMultimap.create();
    final Multimap<WorkspaceStorage, File> files = ArrayListMultimap.create();

    for (IPath storagePath : storagePaths) {
      final List<File> fileList = getDataStore()
          .getDataFiles(start, end, storagePath);
      final IPath workspacePath = plugin.getWorkspacePath(storagePath);
      files.putAll(new WorkspaceStorage(storagePath, workspacePath), fileList);
    }

    for (Map.Entry<WorkspaceStorage, File> entry : files.entries()) {
      for (S list : getCategories(getDataStore().read(entry.getValue()))) {

        final XMLGregorianCalendar date = list.getDate();
        if (date == null) {
          continue; // Ignore invalid data.
        }
        if (startDate.compare(date) <= 0 && date.compare(endDate) <= 0) {
          data.put(entry.getKey(), list);
        }
      }
    }

    return data;
  }

}
