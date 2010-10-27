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

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventGroupType;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A simple implementation of an {@link IAccessor} that calls
 * {@link #createDataNode(Calendar, Object)} every time it encounters a data
 * leaf node. Then returns a collection of created data nodes.
 * 
 * @param <E> The data node type that the subclass is capable of creating.
 * @param <T> The XML leaf node that is defined by this plug-in's schema.
 * @param <S> The XML leaf node holder that categories the nodes according to
 *          dates.
 */
public abstract class AbstractNodeAccessor<E, T, S extends EventGroupType>
    extends AbstractAccessor<E, T, S> {

  private final IMerger<T> merger;

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @param merger The merger for merging XML data nodes.
   * @throws NullPointerException If any arguments are null.
   */
  protected AbstractNodeAccessor(IDataStore store, IMerger<T> merger) {
    super(store);
    this.merger = checkNotNull(merger);
  }

  /**
   * @return The merger for merging XML data nodes.
   */
  public final IMerger<T> getMerger() {
    return merger;
  }

  /**
   * Creates a data node.
   * 
   * @param cal The date of the XML type.
   * @param type The XML type.
   * @return A data node, or null if one cannot be created.
   */
  protected E createDataNode(LocalDate cal, T type) {
    return null;
  }
  
  // TODO
  protected E createDataNode(LocalDate cal, WorkspaceStorage ws, T type) throws Exception {
    return null;
  }

  @Override
  protected Collection<E> filter(Multimap<WorkspaceStorage, S> data) {
    List<E> result = Lists.newLinkedList();
    for (Map.Entry<WorkspaceStorage, S> entry : data.entries()) {
//      List<T> filtered = Lists.newLinkedList();
//      for (T element : getElements(entry.getValue())) {
//        Mergers.merge(getMerger(), filtered, element);//TODO remove
//      }
      LocalDate date = toLocalDate(entry.getValue().getDate());
      for (T element : getElements(entry.getValue())) {
        E node = null;
        try {
          node = createDataNode(date, element);
//          node = createDataNode(date, entry.getKey(), element);
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
   * Gets a collection of types from the given category.
   * 
   * @param category The category.
   * @return A collection of objects.
   */
  protected abstract Collection<T> getElements(S category);
}
