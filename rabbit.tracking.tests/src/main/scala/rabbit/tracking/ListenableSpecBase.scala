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

import org.scalatest.matchers.MustMatchers
import org.scalatest.{ FlatSpec, BeforeAndAfterEach }

import rabbit.tracking.tests.TestImplicits.{ nTimes, funToRunnable }
import rabbit.tracking.tests.TestUtils.doInNewThreads

/**
 * Test trait for `IListenable`.
 *
 * @param A the type of listeners can be added to the listenable
 * @param B the concrete listenable class to be tested
 */
trait ListenableSpecBase[A, B <: IListenable[A]] extends FlatSpec with MustMatchers with BeforeAndAfterEach {

  protected var listenable: B = _

  override def beforeEach() {
    super.beforeEach()
    listenable = create()
  }

  behavior of "Listenable"

  it must "add listener when asked" in {
    val listener = newUniqueListener()
    listenable.addListener(listener)
    getListeners(listenable) must be(set(listener))
  }

  it must "ignore identical listener" in {
    val listener = newUniqueListener()
    listenable.addListener(listener)
    listenable.addListener(listener)
    listenable.addListener(listener)
    getListeners(listenable).size must be(1)
  }

  it must "remove listener when asked" in {
    val listener = newUniqueListener()
    listenable.addListener(listener)
    listenable.removeListener(listener)
    getListeners(listenable) must be(emptySet)
  }

  it must "throw NullPointerException if listener to add is null" in {
    intercept[NullPointerException] {
      listenable.addListener(null.asInstanceOf[A])
    }
  }

  it must "throw NullPointerException if listener to remove is null" in {
    intercept[NullPointerException] {
      listenable.removeListener(null.asInstanceOf[A])
    }
  }

  it must "return listeners as unmodifiable collection" in {
    intercept[UnsupportedOperationException] {
      getListeners(listenable).add(newUniqueListener())
    }
  }

  it must "add listeners correctly from mutiple threads" in {
    10 times {
      doInNewThreads(20, {
        10 times listenable.addListener(newUniqueListener())
      })
    }
    getListeners(listenable).size must be(10 * 20 * 10)
  }

  it must "remove listeners correctly from mutiple threads" in {
    10 times {
      doInNewThreads(20, {
        10 times {
          val listener = newUniqueListener()
          listenable.addListener(listener)
          listenable.removeListener(listener)
        }
      })
      getListeners(listenable).size must be(0)
    }
  }

  it must "return listener collection that is safe to iterate concurrectly" in {
    val error = new AtomicReference[Exception]

    val startSignal = new CountDownLatch(1)
    val doneSignal = new CountDownLatch(2)

    new Thread(() => {
      startSignal.await()
      1000 times listenable.addListener(newUniqueListener())
      doneSignal.countDown()
    }, "modification").start()

    new Thread(() => {
      startSignal.await()
      1000 times {
        try {
          val iterator = getListeners(listenable).iterator
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

  if (supportsCreateWithListeners) {
    it must "throw NullPointerException if creating with null listener" in {
      intercept[NullPointerException] {
        createWithListeners(newUniqueListener(), null.asInstanceOf[A])
      }
    }

    it must "add listeners when creating with listeners" in {
      val listener = new Object
      listenable = createWithListeners(newUniqueListener())
      getListeners(listenable) must be(set(listener))
    }
  }

  private def set[E](a: E*): java.util.Set[E] = {
    val set = new java.util.HashSet[E]
    a.foreach(e => set.add(e))
    set
  }

  /**
   * Boolean to indicate whether the listenable class under test supports
   * constructing with listeners initially. If true, you need to override
   * `createWithListeners` to create a listenable that way for additional tests.
   */
  protected val supportsCreateWithListeners: Boolean

  /**
   * Creates a listenable with default listeners. Override this if
   * `supportsCreateWithListeners` if true.
   */
  protected def createWithListeners(listeners: A*): B =
    throw new UnsupportedOperationException(
      "You need to override this if supportsCreateWithListeners = true")

  /**
   * Creates a listener that can be added to the listenable under test.
   * Each call to this method must return a unique listener and must be safe
   * to call concurrently.
   */
  protected def newUniqueListener(): A

  /**
   * Gets the listeners off the given listenable.
   */
  protected def getListeners(listenable: B): java.util.Collection[A]

  /**
   * Creates a listenable to be tested.
   */
  protected def create(): B
}