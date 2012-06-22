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

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

import java.util.List;

public final class Arrays {

  /**
   * Creates a copy of the given array. If the resulting copy contains null, a
   * {@link NullPointerException} is thrown.
   * 
   * @param array the array to be copied
   * @return a copy of the array
   * @throws NullPointerException if the resulting array contains null
   */
  public static <T> T[] checkedCopy(T[] array) {
    T[] copy = copyOf(array, array.length);
    for (T element : copy) {
      if (element == null) {
        throw new NullPointerException("null contained in "
            + java.util.Arrays.toString(copy));
      }
    }
    return copy;
  }

  /**
   * Creates a list from the given array. If the result contains null, a
   * {@link NullPointerException} is thrown.
   * 
   * @param array the array to be copied
   * @return a list
   * @throws NullPointerException if the resulting list contains null
   */
  public static <T> List<T> checkedCopyAsList(T[] array) {
    return asList(checkedCopy(array));
  }

  private Arrays() {
  }
}
