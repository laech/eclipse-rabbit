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

public abstract class EqualsTestBase {

  private final Object a;
  private final Object objectEqualToA;
  private final Object objectNotEqualToA;

  public EqualsTestBase(
      Object a,
      Object objectEqualToA,
      Object objectNotEqualToA) {
    this.a = a;
    this.objectEqualToA = objectEqualToA;
    this.objectNotEqualToA = objectNotEqualToA;
  }

  @Test public void differentHashCodeIfPropertiesAreDifferent() {
    assertThat(a.hashCode(), is(not(objectNotEqualToA.hashCode())));
  }

  @Test public void equalsToAnotherObjectWithSameProperties() {
    assertThat(a.equals(objectEqualToA), is(true));
  }

  @Test public void equalsToSelf() {
    assertThat(a.equals(a), is(true));
  }

  @Test public void notEqualToDifferentObject() throws Exception {
    assertThat(a.equals(objectNotEqualToA), is(false));
  }

  @Test public void notEqualToNull() throws Exception {
    assertThat(a.equals(null), is(false));
  }

  @Test public void sameHashCodeIfPropertiesAreSame() {
    assertThat(a.hashCode(), is(objectEqualToA.hashCode()));
  }
}
