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

package rabbit.tracking.internal.workbench;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.eclipse.ui.internal.Workbench;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.AbstractTrackerSpec;
import rabbit.tracking.IUserListener;
import rabbit.tracking.IUserMonitorService;
import rabbit.tracking.workbench.AbstractUserTracker;

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
      for (IUserListener listener : listeners) {
        listener.onActive();
      }
    }

    public void notifyInactive() {
      for (IUserListener listener : listeners) {
        listener.onInactive();
      }
    }
  }

  private IUserMonitorService originalService;
  private MockUserMonitorService mockService;

  @Before public void setupMockUserMonitorService() {
    originalService = (IUserMonitorService)Workbench.getInstance()
        .getServiceLocator().getService(IUserMonitorService.class);
    mockService = new MockUserMonitorService();
    Workbench.getInstance().getServiceLocator()
        .registerService(IUserMonitorService.class, mockService);
  }

  @After public void restoreOriginalUserMonitorService() {
    Workbench.getInstance().getServiceLocator()
        .registerService(IUserMonitorService.class, originalService);
  }

  @Test public void detactchesFromUserServiceWhenDisabling() {
    AbstractUserTracker tracker = create();
    tracker.setEnabled(true);
    assertThat(mockService.removeListenerCount, is(0));
    tracker.setEnabled(false);
    mockService.addListenerCount = 0;
    assertThat(mockService.listeners.size(), is(0));
    assertThat(mockService.removeListenerCount, is(1));
    assertThat(mockService.addListenerCount, is(0));
  }

  @Test public void attatchesToUserServiceWhenEnabling() {
    AbstractUserTracker tracker = create();
    tracker.setEnabled(true);
    assertThat(mockService.listeners.size(), is(1));
    assertThat(mockService.addListenerCount, is(1));
    assertThat(mockService.removeListenerCount, is(0));
  }

  /**
   * @return the mock user monitor service registered in the workbench
   */
  protected MockUserMonitorService getMockService() {
    return mockService;
  }

  @Override protected abstract AbstractUserTracker create();
}
