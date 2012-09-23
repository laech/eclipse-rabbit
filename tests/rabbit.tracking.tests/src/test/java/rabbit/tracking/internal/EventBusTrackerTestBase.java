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

package rabbit.tracking.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.AbstractTrackerTestBase;

import com.google.common.eventbus.EventBus;

public abstract class EventBusTrackerTestBase<T extends AbstractTracker>
    extends AbstractTrackerTestBase<T> {

  private EventBus bus;

  @Override protected void init() throws Exception {
    super.init();
    bus = mock(EventBus.class);
  }

  @Test public void registersToEventBusOnStart() {
    T tracker = newTracker(bus);
    try {
      tracker.start();
      verify(bus, only()).register(any());

    } finally {
      tracker.stop();
    }
  }

  @Test public void unregistersFromEventBusOnStop() {
    T tracker = newTracker(bus);
    try {
      tracker.start();
      tracker.stop();

      ArgumentCaptor<Object> arg = ArgumentCaptor.forClass(Object.class);
      InOrder order = inOrder(bus);
      order.verify(bus).register(arg.capture());
      order.verify(bus).unregister(arg.getValue());
      order.verifyNoMoreInteractions();

    } finally {
      tracker.stop();
    }
  }

  protected abstract T newTracker(EventBus bus);
}
