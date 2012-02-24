/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

import rabbit.tracking.ITracker;

import com.google.common.collect.Sets;

public final class TrackingPluginTest {

  private static final Bundle bundle = TrackingPlugin.getDefault().getBundle();

  private TrackingPlugin plugin;

  @Before public void initPlugin() throws Exception {
    plugin = TrackingPlugin.getDefault();
  }

  @After public void resetPlugin() throws Exception {
    bundle.stop();
    bundle.start();
  }

  @Test public void disablesTrackerOnStop() throws Exception {
    ITracker tracker = mock(ITracker.class);
    setTrackers(plugin, tracker);
    bundle.stop();
    verify(tracker).disable();
    verify(tracker).saveData();
  }

  @Test public void disablesTrackersOnShutdown() throws Exception {
    ITracker tracker = mock(ITracker.class);
    setTrackers(plugin, tracker);
    assertThat(plugin.preShutdown(null, false), is(true));
    verify(tracker).disable();
    verify(tracker).saveData();
    verifyNoMoreInteractions(tracker);
  }

  @Test public void savesCurrentDataOnRequest() throws Exception {
    ITracker tracker = mock(ITracker.class);
    setTrackers(plugin, tracker);
    plugin.saveCurrentData();
    verify(tracker, only()).saveData();
  }

  private void setTrackers(TrackingPlugin plugin, ITracker... trackers) {
    try {
      Field f = TrackingPlugin.class.getDeclaredField("trackers");
      f.setAccessible(true);
      f.set(plugin, Sets.newHashSet(trackers));
    } catch (Exception e) {
      Assert.fail(e.getMessage());
    }
  }
}
