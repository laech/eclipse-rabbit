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

package rabbit.tracking.internal.util

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
final class ArraysSpec extends FlatSpec with MustMatchers {

  behavior of "Arrays.checkedCopy"

  it must "throw NullPointerException if the array contains null" in {
    intercept[NullPointerException] {
      Arrays.checkedCopy(Array("notNull", null))
    }
  }

  it must "creates a copy of the original array" in {
    val original = Array("1", "2", "3")
    val copy = Arrays.checkedCopy(original)
    copy must not be theSameInstanceAs(original)
  }

  behavior of "Arrays.checkedCopyAsList"

  it must "throw NullPointerException if the list contains null" in {
    intercept[NullPointerException] {
      Arrays.checkedCopyAsList(Array("notNull", null))
    }
  }

  it must "creates a list from a copy of the original array" in {
    val original = Array("1")
    val list = Arrays.checkedCopyAsList(original)
    list.get(0) must be(original(0))

    original(0) = "2"
    list.get(0) must not be original(0)
  }
}