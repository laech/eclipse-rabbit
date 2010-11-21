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

import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.Mergers;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see AbstractNodeAccessor
 */
public abstract class AbstractNodeAccessorTest<E, T, S extends EventGroupType>
    extends AbstractAccessorTest<E, T, S> {

  protected AbstractNodeAccessor<E, T, S> accessor = create();

  @Test
  public abstract void testCreateDataNode() throws Exception;

  @Test
  public void testGetElements() throws Exception {
    int size = 5;
    S list = createCategory();
    for (int i = 0; i < size; i++) {
      accessor.getElements(list).add(createElement());
    }
    assertEquals(size, accessor.getElements(list).size());
  }

  @Override
  protected abstract AbstractNodeAccessor<E, T, S> create();

  @Override
  protected void assertValues(Collection<E> actual, EventListType events)
      throws Exception {

    IMerger<T> merger = accessor.getMerger();
    SetMultimap<XMLGregorianCalendar, T> map = HashMultimap.create();
    for (S category : accessor.getCategories(events)) {
      if (merger != null)
        Mergers.merge(merger, map.get(category.getDate()),
            getElements(category));
      else
        map.get(category.getDate()).addAll(getElements(category));
    }

    Set<E> expected = Sets.newHashSet();
    for (Entry<XMLGregorianCalendar, Collection<T>> entry : map.asMap().entrySet()) {
      LocalDate date = toLocalDate(entry.getKey());
      for (T type : entry.getValue()) {
        E element = accessor.createDataNode(date, type);
        if (element != null)
          expected.add(element);
      }
    }

    assertEquals(expected.size(), actual.size());
    for (E expectedE : actual) {
      boolean hasMatch = false;
      for (Iterator<E> it = expected.iterator(); it.hasNext();) {
        if (areEqual(expectedE, it.next())) {
          hasMatch = true;
          it.remove();
          break;
        }
      }
      if (!hasMatch) {
        fail();
      }
    }
  }

  /**
   * Method check whether the two objects are equal, it's up to the subclass to
   * determine how to compare the two objects.
   * 
   * @param expected The expected object.
   * @param actual The actual object.
   * @return True if the two objects contains the same properties.
   */
  protected abstract boolean areEqual(E expected, E actual);
}
