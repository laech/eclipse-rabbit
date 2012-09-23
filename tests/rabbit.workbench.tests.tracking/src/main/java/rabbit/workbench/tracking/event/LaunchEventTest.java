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

package rabbit.workbench.tracking.event;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.joda.time.Duration.ZERO;
import static org.joda.time.Instant.now;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import rabbit.tracking.event.TimedEventTest;

public final class LaunchEventTest extends TimedEventTest {

  static final Set<IPath> NO_FILES = emptySet();

  static LaunchEvent eventWith(Duration duration) {
    return new LaunchEvent(now(), duration, mockLaunch(), mockLaunchConfig(),
        mockLaunchConfigType(), NO_FILES);
  }

  static LaunchEvent eventWith(ILaunch launch) {
    return new LaunchEvent(now(), ZERO, launch, mockLaunchConfig(),
        mockLaunchConfigType(), NO_FILES);
  }

  static LaunchEvent eventWith(ILaunchConfiguration launchConfig) {
    return new LaunchEvent(now(), ZERO, mockLaunch(), launchConfig,
        mockLaunchConfigType(), NO_FILES);
  }

  static LaunchEvent eventWith(ILaunchConfigurationType type) {
    return new LaunchEvent(now(), ZERO, mockLaunch(), mockLaunchConfig(),
        type, NO_FILES);
  }

  static LaunchEvent eventWith(Instant instant) {
    return new LaunchEvent(instant, ZERO, mockLaunch(), mockLaunchConfig(),
        mockLaunchConfigType(), NO_FILES);
  }

  static LaunchEvent eventWith(
      Instant instant,
      Duration duration,
      ILaunch launch,
      ILaunchConfiguration config,
      ILaunchConfigurationType type,
      Set<IPath> files) {
    return new LaunchEvent(instant, duration, launch, config, type, files);
  }

  static LaunchEvent eventWith(Set<IPath> files) {
    return new LaunchEvent(now(), ZERO, mockLaunch(), mockLaunchConfig(),
        mockLaunchConfigType(), files);
  }

  static ILaunch mockLaunch() {
    return mock(ILaunch.class);
  }

  static ILaunchConfiguration mockLaunchConfig() {
    return mock(ILaunchConfiguration.class);
  }

  static ILaunchConfigurationType mockLaunchConfigType() {
    return mock(ILaunchConfigurationType.class);
  }

  static Set<IPath> setOfFilePaths(String... paths) {
    Set<IPath> set = newHashSet();
    for (String path : paths)
      set.add(new Path(path));
    return set;
  }

  @Test public void returnsTheFiles() {
    Set<IPath> files = setOfFilePaths("/a/b", "/c/d");
    assertThat(eventWith(files).files(), is(files));
  }

  @Test public void returnsTheLaunch() throws CoreException {
    ILaunch launch = mockLaunch();
    LaunchEvent e = eventWith(launch);
    assertThat(e.launch(), is(launch));
  }

  @Test public void returnsTheLaunchConfiguration() throws CoreException {
    ILaunchConfiguration config = mockLaunchConfig();
    LaunchEvent e = eventWith(config);
    assertThat(e.launchConfig(), is(config));
  }

  @Test public void returnsTheLaunchConfigurationType() throws CoreException {
    ILaunchConfigurationType type = mockLaunchConfigType();
    LaunchEvent e = eventWith(type);
    assertThat(e.launchConfigType(), is(type));
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutFilesCollection() {
    eventWith((Set<IPath>)null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutLaunch() {
    eventWith((ILaunch)null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutLaunchConfig() {
    eventWith((ILaunchConfiguration)null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutLaunchConfigType() throws Exception {
    eventWith((ILaunchConfigurationType)null);
  }

  @Override protected LaunchEvent newEvent(Instant instant, Duration duration) {
    return eventWith(instant, duration, mockLaunch(), mockLaunchConfig(),
        mockLaunchConfigType(), NO_FILES);
  }

}
