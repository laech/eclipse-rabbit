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
package rabbit.ui.internal.util;

import com.google.common.base.Objects;

import javax.annotation.Nullable;

/**
 * Class to hold a pair of objects.
 * 
 * @param <F> The first element type.
 * @param <S> The second element type.
 */
public final class Pair<F, S> {

  private F first;
  private S second;
  
  /**
   * Creates a new pair.
   * @param first The first element, may be null.
   * @param second The second element, may be null.
   * @return A pair object.
   */
  public static <F, S> Pair<F, S> create(F first, S second) {
    return new Pair<F, S>(first, second);
  }

  /**
   * Constructor.
   * 
   * @param first The first element, may be null.
   * @param second The second element, may be null.
   */
  private Pair(@Nullable F first, @Nullable S second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Gets the first element.
   * 
   * @return The first element.
   */
  public F getFirst() {
    return first;
  }

  /**
   * Gets the second element.
   * 
   * @return The second element.
   */
  public S getSecond() {
    return second;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getFirst(), getSecond());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    Pair<?, ?> pair = (Pair<?, ?>) obj;
    return Objects.equal(pair.getFirst(), getFirst())
        && Objects.equal(pair.getSecond(), getSecond());
  }
}
