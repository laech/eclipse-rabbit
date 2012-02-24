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

package rabbit.tracking;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Collection;

import org.junit.Test;

/**
 * Class for testing {@link AbstractUserTracker}, the workbench
 * {@link IUserMonitorService} will be replaced by a
 * {@link MockUserMonitorService}.
 */
public abstract class AbstractUserTrackerSpec extends AbstractTrackerSpec {

  /*
   * This class exits as the base test class for all trackers that extend from
   * AbstractUserTracker. The test methods defined here are also for making sure
   * super.onEnable() & super.onDisable() are called from subclasses no matter
   * how deep down the hierarchy they are, if they don't call super, these tests
   * will fail. The main purpose of such test is to make sure super class
   * functionalities are not lost when onEnable() & onDisable() are overridden
   * and super is not called.
   */

  protected static final class MockUserMonitorService
      implements IUserMonitorService {

    final Collection<IUserListener> listeners = newHashSet();
    boolean active;
    int addListenerCount;
    int removeListenerCount;

    @Override public void addListener(IUserListener listener) {
      listeners.add(listener);
      addListenerCount++;
    }

    @Override public void removeListener(IUserListener listener) {
      listeners.remove(listener);
      removeListenerCount++;
    }

    @Override public boolean isUserActive() {
      return active;
    }

    public void notifyActive() {
      active = true;
      for (IUserListener listener : listeners) {
        listener.onActive();
      }
    }

    public void notifyInactive() {
      active = false;
      for (IUserListener listener : listeners) {
        listener.onInactive();
      }
    }
  }

  private MockUserMonitorService service;
  private AbstractUserTracker tracker;

  @Override public void setup() throws Exception {
    super.setup();
    service = new MockUserMonitorService();
    tracker = create(service);
  }

  @Override public void teardown() throws Exception {
    super.teardown();
    tracker.disable();
  }

  @Test public void detactchesFromUserServiceWhenDisabling() {
    tracker.enable();
    assertThat(service.removeListenerCount, is(0));
    tracker.disable();
    service.addListenerCount = 0;
    assertThat(service.listeners.size(), is(0));
    assertThat(service.removeListenerCount, is(1));
    assertThat(service.addListenerCount, is(0));
  }

  @Test public void attatchesToUserServiceWhenEnabling() {
    tracker.enable();
    assertThat(service.listeners.size(), is(1));
    assertThat(service.addListenerCount, is(1));
    assertThat(service.removeListenerCount, is(0));
  }

  /**
   * @return the mock user monitor service
   */
  protected MockUserMonitorService getMockService() {
    return service;
  }

  @Override protected final AbstractUserTracker create() {
    return create(new MockUserMonitorService());
  }

  protected abstract AbstractUserTracker create(IUserMonitorService service);
}
