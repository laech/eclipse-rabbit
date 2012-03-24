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

import java.util.Collections.emptySet
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.CountDownLatch

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import rabbit.tracking.tests.TestImplicits.{ nTimes, funToRunnable }
import rabbit.tracking.tests.TestUtils.doInNewThreads

@RunWith(classOf[JUnitRunner])
final class AbstractListenableTrackerSpec extends AbstractTrackerSpecBase {

  protected type Tracker = AbstractListenableTracker[Any]

  behavior of "AbstractListenableTracker"

  it must "add listener when asked" in {
    val listener = "listener #1"
    tracker.addListener(listener)
    tracker.getListeners must be(set(listener))
  }

  it must "remove listener when asked" in {
    val listener = "listener #1"
    tracker.addListener(listener)
    tracker.removeListener(listener)
    tracker.getListeners must be(emptySet)
  }

  it must "throw NullPointerException if listener to add is null" in {
    intercept[NullPointerException] {
      tracker.addListener(null)
    }
  }

  it must "throw NullPointerException if listener to remove is null" in {
    intercept[NullPointerException] {
      tracker.removeListener(null)
    }
  }

  it must "throw NullPointerException if creating with null listener" in {
    intercept[NullPointerException] {
      createWithListeners("listener #1", null)
    }
  }

  it must "add listeners when creating with listeners" in {
    val listener = new Object
    tracker = createWithListeners(listener)
    tracker.getListeners must be(set(listener))
  }

  it must "return listeners as unmodifiable collection" in {
    intercept[UnsupportedOperationException] {
      tracker.getListeners.add(new Object)
    }
  }

  it must "add listeners correctly from mutiple threads" in {
    10 times {
      doInNewThreads(20, {
        10 times tracker.addListener(new Object)
      })
    }
    tracker.getListeners.size must be(10 * 20 * 10)
  }

  it must "remove listeners correctly from mutiple threads" in {
    10 times {
      doInNewThreads(20, {
        10 times {
          val listener = new Object
          tracker.addListener(listener)
          tracker.removeListener(listener)
        }
      })
      tracker.getListeners.size must be(0)
    }
  }

  it must "return listener collection that is safe to iterate concurrectly" in {
    val error = new AtomicReference[Exception]

    val startSignal = new CountDownLatch(1)
    val doneSignal = new CountDownLatch(2)

    new Thread(() => {
      startSignal.await()
      1000 times tracker.addListener(new Object)
      doneSignal.countDown()
    }, "modification").start()

    new Thread(() => {
      startSignal.await()
      1000 times {
        try {
          val iterator = tracker.getListeners.iterator
          while (iterator.hasNext) iterator.next()
        } catch {
          case e: Exception => error.set(e); doneSignal.countDown()
        }
      }
      doneSignal.countDown()
    }, "iteration").start()

    startSignal.countDown()
    doneSignal.await()

    error.get must be(null)
  }

  private def set(a: Any*): java.util.Set[Any] = {
    val set = new java.util.HashSet[Any]
    a.foreach(e => set.add(e))
    set
  }

  protected def create() = createWithListeners()

  private def createWithListeners(listeners: Any*) =
    new AbstractListenableTracker(listeners: _*) {
      override protected def onEnable() {}
      override protected def onDisable() {}
    }
}