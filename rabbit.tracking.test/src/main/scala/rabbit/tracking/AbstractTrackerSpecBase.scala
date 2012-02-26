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

package rabbit.tracking

import org.scalatest.{ FunSpec, BeforeAndAfter }
import org.scalatest.matchers.MustMatchers
import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfterEach

trait AbstractTrackerSpecBase extends FlatSpec with MustMatchers with BeforeAndAfterEach {

  protected type Tracker <: AbstractTracker

  protected var tracker: Tracker = _

  override def beforeEach() {
    tracker = create()
  }

  override def afterEach() {
    tracker.disable()
  }

  protected def create(): Tracker

  behavior of "AbstractTracker"

  it must "be disabled by default" in {
    tracker.isEnabled must be(false)
  }

  it must "be enabled when set to enable" in {
    tracker.enable()
    tracker.isEnabled must be(true)
  }

  it must "be disabled when set to disable" in {
    tracker.disable()
    tracker.isEnabled must be(false)
  }
}