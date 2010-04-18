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

import static rabbit.data.internal.xml.DatatypeUtil.toLocalDate;

import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.Mergers;
import rabbit.data.internal.xml.schema.events.EventGroupType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.xml.datatype.XMLGregorianCalendar;

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
public abstract class AbstractDataNodeAccessor<E, T, S extends EventGroupType>
    extends AbstractAccessor<E, T, S> {

  @CheckForNull
  protected final IMerger<T> merger;

  public AbstractDataNodeAccessor() {
    merger = createMerger();
  }

  @Override
  protected ImmutableCollection<E> filter(List<S> data) {

    SetMultimap<XMLGregorianCalendar, T> multimap = HashMultimap.create();
    for (S category : data) {

      if (merger != null)
        Mergers.merge(merger, multimap.get(category.getDate()),
            getElements(category));
      else
        multimap.get(category.getDate()).addAll(getElements(category));
    }

    ImmutableSet.Builder<E> result = ImmutableSet.builder();
    for (Entry<XMLGregorianCalendar, Collection<T>> entry : multimap.asMap()
        .entrySet()) {

      LocalDate date = toLocalDate(entry.getKey());
      for (T type : entry.getValue()) {
        E element = createDataNode(date, type);
        if (element != null)
          result.add(element);
      }
    }
    return result.build();
  }

  /**
   * Creates a merger for merging identical elements.
   * 
   * @return A merger, or null if all elements are to be treated uniquely.
   */
  protected abstract IMerger<T> createMerger();

  /**
   * Creates a data node.
   * 
   * @param cal The date of the XML type.
   * @param type The XML type.
   * @return A data node, or null if one cannot be created.
   */
  @CheckForNull
  protected abstract E createDataNode(LocalDate cal, T type);

  /**
   * Gets a collection of types from the given category.
   * 
   * @param category The category.
   * @return A collection of objects.
   */
  protected abstract Collection<T> getElements(S category);
}
