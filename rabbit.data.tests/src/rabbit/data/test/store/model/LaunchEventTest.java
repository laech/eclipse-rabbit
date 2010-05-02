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
package rabbit.data.test.store.model;

import rabbit.data.store.model.LaunchEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEvent
 */
@SuppressWarnings("restriction")
public class LaunchEventTest extends ContinuousEventTest {

  private static class LaunchConfigurationForTest extends LaunchConfiguration {
    protected LaunchConfigurationForTest(String name) {
      super(name, null);
    }
  }

  @Test
  public void testConstructor_copiesFileIds() {
    Set<IPath> filePaths = new HashSet<IPath>();
    filePaths.add(new Path("/a"));
    filePaths.add(new Path("/b"));
    filePaths.add(new Path("/c"));
    LaunchEvent event = new LaunchEvent(new DateTime(), 18, new Launch(
        new LaunchConfigurationForTest("a"), ILaunchManager.DEBUG_MODE, null),
        new LaunchConfigurationForTest("asdf"), filePaths);

    assertFalse(filePaths == event.getFilePaths());

    filePaths.add(new Path("/Should not effect the collection in the event."));
    assertFalse(filePaths.size() == event.getFilePaths().size());

    filePaths.clear();
    assertFalse(event.getFilePaths().isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_fileIdsNull() {
    new LaunchEvent(new DateTime(), 1, new Launch(
        new LaunchConfigurationForTest("asdfdsf"), ILaunchManager.DEBUG_MODE,
        null), new LaunchConfigurationForTest("adfsdfdsf"), null);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_launchConfigNull() {
    new LaunchEvent(new DateTime(), 10, new Launch(
        new LaunchConfigurationForTest("a"), ILaunchManager.DEBUG_MODE, null),
        null, Collections.<IPath> emptySet());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_launchNull() {
    new LaunchEvent(new DateTime(), 10, null,
        new LaunchConfigurationForTest("Adfd222"), Collections
            .<IPath> emptySet());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetFileIds_unmodifiable() {
    LaunchEvent event = new LaunchEvent(new DateTime(), 32, new Launch(
        new LaunchConfigurationForTest("a"), ILaunchManager.DEBUG_MODE, null),
        new LaunchConfigurationForTest("a"), new HashSet<IPath>());
    event.getFilePaths().add(new Path("/Should throw exception."));
  }

  @Test
  public void testGetLaunch() {
    ILaunch launch = new Launch(new LaunchConfigurationForTest("a"),
        ILaunchManager.DEBUG_MODE, null);
    LaunchEvent event = new LaunchEvent(new DateTime(), 10, launch,
        new LaunchConfigurationForTest("bbb"), Collections.<IPath> emptySet());

    assertSame(launch, event.getLaunch());
  }

  @Test
  public void testGetLaunchConfiguration() {
    ILaunchConfiguration config = new LaunchConfigurationForTest("b");
    LaunchEvent event = new LaunchEvent(new DateTime(), 101,
        new Launch(config, ILaunchManager.DEBUG_MODE, null), config,
        Collections.<IPath> emptySet());

    assertSame(config, event.getLaunchConfiguration());
  }

}