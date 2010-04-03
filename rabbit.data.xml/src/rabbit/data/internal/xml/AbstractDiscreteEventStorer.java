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

import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.store.model.DiscreteEvent;

import java.util.List;

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
public abstract class AbstractDiscreteEventStorer<E extends DiscreteEvent, T, S extends EventGroupType>
    extends AbstractStorer<E, T, S> {

  /**
   * Sole constructor.
   */
  public AbstractDiscreteEventStorer() {
  }

  /**
   * Checks whether the given objects has the same id.
   * <p>
   * Other properties are ignored. In other words, checks to see whether the two
   * objects can be merged without loosing its unique identity.
   * </p>
   * 
   * @param x1 A java object representing an XML element.
   * @param x2 A java object representing an XML element.
   * @return true If the two object has the same id.
   */
  protected abstract boolean hasSameId(T x1, T x2);

  @Override
  protected void merge(List<T> xList, E event) {

    T newType = newXmlType(event);

    boolean done = false;
    for (T xmlType : xList) {
      if (hasSameId(xmlType, newType)) {
        merge(xmlType, newType);
        done = true;
        break;
      }
    }

    if (!done) {
      xList.add(newType);
    }
  }

  @Override
  protected void merge(List<T> mainList, List<T> newList) {

    for (T newType : newList) {
      boolean done = false;

      for (T mainType : mainList) {
        if (hasSameId(mainType, newType)) {
          merge(mainType, newType);
          done = true;
          break;
        }
      }

      if (!done) {
        mainList.add(newType);
      }
    }
  }

  /**
   * Merges the data of the second parameter into the first parameter.
   * 
   * @param main The object to merge into.
   * @param x The object to merge from.
   */
  protected abstract void merge(T main, T x);

  /**
   * Creates a new XML object type from the given event.
   * 
   * @param e The event.
   * @return A new XML object type.
   */
  protected abstract T newXmlType(E e);
}
