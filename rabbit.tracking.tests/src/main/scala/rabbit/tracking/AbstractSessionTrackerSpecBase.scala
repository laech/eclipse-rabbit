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

import java.lang.Thread.sleep

import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, inOrder, doAnswer, never }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.util.{ Recorder, IRecorder }

/*
 * Base spec for `AbstractSessionTracker` implementations.
 * See `PartSessionTrackerSpec` for example implementation.
 */
@RunWith(classOf[JUnitRunner])
abstract class AbstractSessionTrackerSpecBase[T: Manifest, L <: AnyRef]
  extends AbstractTrackerSpecBase {

  protected final class Expected {
    var preStart: Instant = _
    var postStart: Instant = _
    var preEnd: Instant = _
    var postEnd: Instant = _
    var target: T = _
  }

  protected final class Actual {
    var instant: Instant = _
    var duration: Duration = _
    var target: T = _
  }

  protected var recorder: IRecorder[T] = _
  protected var monitor: IUserMonitor = _
  protected var monitorListeners: Set[IUserListener] = _

  override def beforeEach {
    recorder = Recorder.create()
    monitorListeners = Set.empty
    monitor = mock[IUserMonitor]
    doAnswer({ invocation: InvocationOnMock =>
      monitorListeners += invocation.getArguments()(0).asInstanceOf[IUserListener]
    }).when(monitor).addListener(any[IUserListener])

    super.beforeEach
  }

  behavior of classOf[AbstractSessionTracker[_, _]].getSimpleName

  it must "record target focused duration on target change" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.start

    val expected = new Expected
    expected.preStart = now
    expected.target = changeTarget
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    changeTarget
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "record target focused duration on stop" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.start

    val expected = new Expected
    expected.preStart = now
    expected.target = changeTarget
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    tracker.stop
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "be able to record without a user monitor" in {
    val (listener, actual) = mockListenerWithResult
    val tracker = create(monitor = null)
    tracker.start
    tracker.addListener(listener)

    val expected = changeTarget
    tracker.stop

    actual.target must be(expected)
  }

  it must "not notify if stopped" in {
    val (listener, _) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.stop
    changeTarget
    changeTarget
    tracker.stop
    verifyZeroInteractions(listener)
  }

  it must "not notify removed listeners" in {
    val (listener, _) = mockListenerWithResult
    tracker.start
    tracker.addListener(listener)
    tracker.removeListener(listener)
    changeTarget
    tracker.stop
    verifyZeroInteractions(listener)
  }

  it must "start recording on start if there is a target" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)

    val expected = new Expected
    expected.target = changeTarget

    expected.preStart = now
    tracker.start
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    changeTarget
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "start recording if there is a target on user active" in {
    val expected = changeTarget
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.start
    sleep(2)
    tracker.stop
    actual.target must be(expected)
  }

  it must "not start recording if there is no target on user active" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    removeAllTargets

    tracker.start
    sleep(2)
    tracker.stop

    actual.target.asInstanceOf[AnyRef] must be(null)
  }

  it must "stop recording on user inactive" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.start

    val expected = new Expected
    expected.preStart = now
    expected.target = changeTarget
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    monitorListeners.foreach(_.onInactive)
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "stop recording on stop" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.start

    val expected = new Expected
    expected.preStart = now
    expected.target = changeTarget
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    tracker.stop
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "detactch listener from user monitor when stopping" in {
    val monitor = mock[IUserMonitor]
    val tracker = create(monitor = monitor)
    tracker.start
    tracker.stop
    verify(monitor).removeListener(notNull(classOf[IUserListener]))
  }

  it must "attatch listener to user monitor when starting" in {
    val monitor = mock[IUserMonitor]
    val tracker = create(monitor = monitor)
    tracker.start
    verify(monitor).addListener(notNull(classOf[IUserListener]))
  }

  it must "stop recorder when stopping" in {
    val recorder = mock[IRecorder[T]]
    val tracker = create(recorder = recorder)
    tracker.start
    tracker.stop
    val order = inOrder(recorder)
    order.verify(recorder).stop
    order.verify(recorder, never).start(any[T])
  }

  it must "throw NullPointerException if constructing without a recorder" in {
    intercept[NullPointerException] {
      create(recorder = null)
    }
  }

  it must "not throw exception if constructing without a user monitor" in {
    create(monitor = null)
  }

  override protected type Tracker <: AbstractSessionTracker[T, L]

  override protected final def create(): Tracker = create(recorder, monitor)

  protected def create(
    recorder: IRecorder[T] = recorder,
    monitor: IUserMonitor = monitor): Tracker

  protected final def verifyEvent(actual: Actual, expected: Expected) {
    actual.target must be(expected.target)

    val start = actual.instant.getMillis
    start must be >= expected.preStart.getMillis
    start must be <= expected.postStart.getMillis

    val end = start + actual.duration.getMillis
    end must be >= expected.preEnd.getMillis
    end must be <= expected.postEnd.getMillis
  }

  /**
   * Creates a tuple of a listener, and a result object. The fields of the
   * result object should be updated when the listener is called.
   */
  protected def mockListenerWithResult(): (L, Actual)

  /**
   * Changes the current target in the workbench.
   * @return the new target
   */
  protected def changeTarget(): T

  /**
   * Removes all targets from the workbench.
   */
  protected def removeAllTargets(): Unit

}