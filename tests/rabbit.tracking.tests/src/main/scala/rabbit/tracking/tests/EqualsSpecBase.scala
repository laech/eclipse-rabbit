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

package rabbit.tracking.tests

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.TableFor2
import org.scalatest.matchers.MustMatchers
import org.scalatest.FlatSpec

trait EqualsSpecBase extends FlatSpec with MustMatchers with TableDrivenPropertyChecks {

  behavior of "Object"

  it must "return same hash code if properties are same" in {
    equalObject.hashCode must be(equalObject.hashCode)
  }

  it must "return different hash codes if any properties are different" in {
    forAll(differences) { (a, b) =>
      a.hashCode must not be b.hashCode
    }
  }

  it must "equal to itself" in {
    val any = equalObject
    any must be(any)
  }

  it must "equal to another object with same properties" in {
    equalObject must be(equalObject)
  }

  it must "not equal to a different object" in {
    forAll(differences) { (a, b) =>
      a must not be b
    }
  }

  it must "not equal to null" in {
    equalObject.equals(null) must be(false)
  }

  /**
   * Creates a table of pair elements, each pair are different in same way.
   */
  protected def differences(): TableFor2[Any, Any]

  /**
   * Creates elements that are always equal to each other.
   */
  protected def equalObject(): Any
}