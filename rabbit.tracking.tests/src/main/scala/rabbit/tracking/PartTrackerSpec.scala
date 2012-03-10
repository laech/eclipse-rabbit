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

import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, only, never, inOrder, atLeastOnce }
import org.mockito.BDDMockito.given
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.junit.JUnitRunner

import rabbit.tracking.PartTracker.IPartFocusListener
import rabbit.tracking.tests.WorkbenchTestUtil.{ openWindow, openRandomPart, hide, closeAllParts, close, activate }

@RunWith(classOf[JUnitRunner])
final class PartTrackerSpec extends AbstractTrackerSpecBase {

  type Tracker = PartTracker

  private var window: IWorkbenchWindow = _
  private var listener: IPartFocusListener = _

  override def beforeEach() {
    super.beforeEach()
    listener = mockListener()
    tracker.addListener(listener)
    closeAllParts();
  }

  override def afterEach() {
    super.afterEach()
    if (window != null && window.getShell != null) {
      close(window)
      window = null
    }
  }

  behavior of "PartTracker"

  it must "notify part focused when a part is already focused when enabled" in {
    val part = activate(openRandomPart())
    tracker.enable()
    verify(listener, only).onPartFocused(part)
  }

  it must "notify part focused due to part opened" in {
    tracker.enable()
    val part = openRandomPart()
    verify(listener).onPartFocused(part)
  }

  it must "notify part focused due to part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()

    activate(part2)
    verify(listener).onPartFocused(part2)
  }

  it must "notify part unfocused due to new part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()

    activate(part2)
    verify(listener).onPartUnfocused(part1)
  }

  it must "notify part unfocused due to new part opened" in {
    tracker.enable()
    val part = openRandomPart()

    openRandomPart()
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to window unfocused" in {
    tracker.enable()
    val part = openRandomPart()

    window = openWindow()
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to part closed" in {
    val part = openRandomPart();
    tracker.enable()

    hide(part)
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to window closed" in {
    window = openWindow()
    var part = openRandomPart(window)
    tracker.enable()
    verify(listener, never).onPartUnfocused(whatever)

    close(window)
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unforcused on old part before notifying part focused on new part" in {
    tracker.enable()
    openRandomPart()
    openRandomPart()

    val order = inOrder(listener)
    order.verify(listener).onPartUnfocused(whatever)
    order.verify(listener).onPartFocused(whatever)
    order.verifyNoMoreInteractions()
  }

  it must "not notify when disabled" in {
    tracker.disable()
    openRandomPart()
    openRandomPart()
    verifyZeroInteractions(listener)
  }

  it must "track newly opened window" in {
    tracker.enable()
    window = openWindow()
    openRandomPart(window)
    verify(listener, atLeastOnce).onPartFocused(whatever)
  }

  it must "add listener when asked" in {
    tracker.enable()
    val listener = mockListener()
    tracker.addListener(listener)
    openRandomPart()
    verify(listener).onPartFocused(whatever)
  }

  it must "remove listener when asked" in {
    closeAllParts()
    tracker.enable()
    tracker.removeListener(listener)
    openRandomPart()
    verifyZeroInteractions(listener)
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
      create(mockListener(), null)
    }
  }

  it must "add listeners to be notified when creating with listeners" in {
    tracker = create(listener)
    tracker.enable()
    openRandomPart()
    verify(listener).onPartFocused(whatever)
  }

  it must "ignore identical listeners if one already added" in {
    val listener1 = equalsToEveryThingListener()
    val listener2 = equalsToEveryThingListener()

    tracker.enable()
    tracker.removeListener(listener)
    tracker.addListener(listener1)
    tracker.addListener(listener2)
    openRandomPart()

    listener1.called must be(true)
    listener2.called must be(false)
  }

  override protected def create() = PartTracker.get()

  private def create(listeners: IPartFocusListener*) = PartTracker.withListeners(listeners: _*)

  private def whatever = any[IWorkbenchPart]

  private def mockListener() = mock[IPartFocusListener]

  private def equalsToEveryThingListener() = new IPartFocusListener {
    var called = false
    override def onPartFocused(part: IWorkbenchPart) { called = true }
    override def onPartUnfocused(part: IWorkbenchPart) { called = true }
    override def hashCode = 0
    override def equals(a: Any) = true
  }
}
