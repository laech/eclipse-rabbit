/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.merge;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

/**
 * Utility class for working with {@link IMerger}.
 */
public class Mergers {

  /**
   * Merges a collection of elements into another collection. If any of the
   * element is not merged with another element, it will be added to the
   * collection instead.
   * 
   * @param merger The merger to use.
   * @param mergeTo The collection to merge the elements to.
   * @param elementsToMerge The elements to be merged into the collection.
   */
  public static <T> void merge(@Nonnull IMerger<T> merger,
      @Nonnull Collection<T> mergeTo, @Nonnull Collection<T> elementsToMerge) {

    checkNotNull(merger);
    checkNotNull(mergeTo);
    checkNotNull(elementsToMerge);
    
    for (T element : elementsToMerge) {
      merge(merger, mergeTo, element);
    }
  }

  /**
   * Merges a element into a collection. If the element is not merged with
   * another element, it will be added to the collection instead.
   * 
   * @param merger The merger to use.
   * @param mergeTo The collection to merge the element to.
   * @param elementsToMerge The element to be merged into the collection.
   */
  public static <T> void merge(@Nonnull IMerger<T> merger,
      @Nonnull Collection<T> mergeTo, @Nonnull T elementToMerge) {

    checkNotNull(merger);
    checkNotNull(mergeTo);
    checkNotNull(elementToMerge);

    T mergedElement = null;
    for (Iterator<T> iterator = mergeTo.iterator(); iterator.hasNext();) {
      T element = iterator.next();
      if (merger.isMergeable(element, elementToMerge)) {
        mergedElement = merger.merge(element, elementToMerge);

        // Removes the old one, the new one will be added after the loop:
        iterator.remove();
        break;
      }
    }

    if (mergedElement == null)
      mergeTo.add(elementToMerge);
    else
      mergeTo.add(mergedElement);
  }
}
