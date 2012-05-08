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

import java.util.concurrent.atomic.AtomicInteger

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
final class AbstractListenableTrackerSpec extends AbstractTrackerSpecBase
  with ListenableSpecBase[String, AbstractListenableTracker[String]] {

  private val listenerCount = new AtomicInteger

  override protected type Tracker = AbstractListenableTracker[String]

  override protected def newUniqueListener() = "listener #" + listenerCount.getAndIncrement()

  override protected def getListeners(listenable: AbstractListenableTracker[String]) =
    listenable.getListeners()

  override protected def create() = new AbstractListenableTracker[String] {
    override protected def onStart() {}
    override protected def onStop() {}
  }
}