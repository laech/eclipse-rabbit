/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking.internal.util;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public final class Sets {

  /**
   * Creates a new {@link CopyOnWriteArraySet} from the given collection.
   * 
   * @param elements the initial elements for the set
   * @throws a new set containing the elements
   */
  public static <E> CopyOnWriteArraySet<E> newCopyOnWriteSet(
      Collection<? extends E> elements) {
    return new CopyOnWriteArraySet<E>(elements);
  }

  private Sets() {
  }
}
