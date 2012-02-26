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

/**
 * Class for testing {@link AbstractUserTracker}, the workbench
 * {@link IUserMonitorService} will be replaced by a
 * {@link MockUserMonitorService}.
 */
trait AbstractUserTrackerSpecBase extends AbstractTrackerSpecBase {

  /*
   * This class exits as the base test class for all trackers that extend from
   * AbstractUserTracker. The test methods defined here are also for making sure
   * super.onEnable() & super.onDisable() are called from subclasses no matter
   * how deep down the hierarchy they are, if they don't call super, these tests
   * will fail. The main purpose of such test is to make sure super class
   * functionalities are not lost when onEnable() & onDisable() are overridden
   * and super is not called.
   */

  protected final class MockUserMonitorService extends IUserMonitorService {
    var listeners = collection.immutable.Set.empty[IUserListener]
    private var _active = false
    private var _addListenerCount = 0
    private var _removeListenerCount = 0

    def removeListenerCount = synchronized { _removeListenerCount }
    def addListenerCount = synchronized { _addListenerCount }
    def addListenerCount_=(count: Int) {
      synchronized { _addListenerCount = count }
    }

    override def addListener(listener: IUserListener) {
      synchronized {
        listeners += listener
        _addListenerCount += 1
      }
    }

    override def removeListener(listener: IUserListener) {
      synchronized {
        listeners -= listener
        _removeListenerCount += 1;
      }
    }

    override def isUserActive = synchronized { _active }

    def notifyActive() {
      synchronized {
        _active = true
      }
      listeners.foreach(_.onActive())
    }

    def notifyInactive() {
      synchronized {
        _active = false
      }
      listeners.foreach(_.onInactive())
    }
  }

  protected type Tracker <: AbstractUserTracker

  behavior of "AbstractUserTracker"

  it must "detactch from user service when disabling" in {
    val service = getMockService(tracker)
    tracker.enable()
    service.removeListenerCount must be(0)
    service.addListenerCount = 0

    tracker.disable()
    service.listeners must have size 0
    service.removeListenerCount must be(1)
    service.addListenerCount must be(0)
  }

  it must "attatch to user service when enabling" in {
    val service = getMockService(tracker)
    tracker.enable()
    service.listeners must have size 1
    service.addListenerCount must be(1)
    service.removeListenerCount must be(0)
  }

  override protected def create() = create(new MockUserMonitorService)

  protected def getMockService(tracker: Tracker) =
    tracker.getUserMonitorService.asInstanceOf[MockUserMonitorService]

  protected def create(service: IUserMonitorService): Tracker
}