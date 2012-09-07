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

package rabbit.tracking.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Test;
import org.junit.runners.Parameterized;

public abstract class EqualsTestBase {

  private final Object b;
  private final Object a1;
  private final Object a2;

  /**
   * a1 is equal to a2, b is different than a1 and a2, this constructor is
   * designed to used with {@link Parameterized} tests.
   */
  public EqualsTestBase(Object a1, Object a2, Object b) {
    this.a1 = a1;
    this.a2 = a2;
    this.b = b;
  }

  @Test public void sameHashCodeIfPropertiesAreSame() {
    assertThat(a1.hashCode(), is(a2.hashCode()));
  }

  @Test public void differentHashCodeIfPropertiesAreDifferent() {
    assertThat(a1.hashCode(), is(not(b.hashCode())));
  }

  @Test public void equalsToSelf() {
    assertThat(a1.equals(a1), is(true));
  }

  @Test public void equalsToAnotherObjectWithSameProperties() {
    assertThat(a1.equals(a2), is(true));
  }

  @Test public void notEqualToDifferentObject() throws Exception {
    assertThat(a1.equals(b), is(false));
  }

  @Test public void notEqualToNull() throws Exception {
    assertThat(a1.equals(null), is(false));
  }
}
