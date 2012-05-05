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

package rabbit.tracking.util;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.IEventListener;

public final class EventListenerSupportTest {

  private static class Tester extends EventListenerSupport<Object> {

    IEventListener<Object> listener;

    @SuppressWarnings("unchecked")//
    Tester() {
      listener = mock(IEventListener.class);
    }

    @SuppressWarnings("unchecked")//
    @Override protected Iterable<IEventListener<Object>> getListeners() {
      return asList(listener);
    }
  }

  private Tester support;

  @Before public void setup() {
    support = new Tester();
  }

  @Test public void notifiesOnEvent() {
    Object event = new Object();
    support.onEvent(event);
    verify(support.listener, only()).onEvent(event);
  }
}
