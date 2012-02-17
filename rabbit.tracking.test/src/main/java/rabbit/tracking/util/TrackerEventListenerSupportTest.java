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

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.ITrackerEventListener;

public final class TrackerEventListenerSupportTest {

  private static class TrackerListenerSupportTester extends TrackerEventListenerSupport<Object> {

    ITrackerEventListener<Object> listener;

    @SuppressWarnings("unchecked")//
    TrackerListenerSupportTester() {
      listener = mock(ITrackerEventListener.class);
    }

    @SuppressWarnings("unchecked")//
    @Override protected Collection<ITrackerEventListener<Object>> getListeners() {
      return asList(listener);
    }
  }

  private TrackerListenerSupportTester support;

  @Before public void setup() {
    support = new TrackerListenerSupportTester();
  }

  @Test public void notifiesOnSave() {
    support.notifyOnSaveData();
    verify(support.listener, only()).onSaveData();
  }

  @Test public void notifiesOnEnabled() {
    support.notifyOnEnabled();
    verify(support.listener, only()).onEnabled();
  }

  @Test public void notifiesOnDisabled() {
    support.notifyOnDisabled();
    verify(support.listener, only()).onDisabled();
  }

  @Test public void notifiesOnEvent() {
    Object event = new Object();
    support.notifyOnEvent(event);
    verify(support.listener, only()).onEvent(event);
  }
}
