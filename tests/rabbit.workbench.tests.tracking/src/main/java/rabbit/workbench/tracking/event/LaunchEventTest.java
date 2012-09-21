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
import static org.mockito.BDDMockito.given;
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
    try {
      return new LaunchEvent(now(), duration, mockLaunch(), NO_FILES);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }
  }

  static LaunchEvent eventWith(ILaunch launch) {
    try {
      return new LaunchEvent(now(), ZERO, launch, NO_FILES);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }
  }

  static LaunchEvent eventWith(Instant instant) {
    try {
      return new LaunchEvent(instant, ZERO, mockLaunch(), NO_FILES);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }
  }

  static LaunchEvent eventWith(
      Instant instant, Duration duration, ILaunch launch, Set<IPath> files) {
    try {
      return new LaunchEvent(instant, duration, launch, files);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }
  }

  static LaunchEvent eventWith(Set<IPath> files) {
    try {
      return new LaunchEvent(now(), ZERO, mockLaunch(), files);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }
  }

  static ILaunch mockLaunch() {
    ILaunchConfigurationType type = mock(ILaunchConfigurationType.class);
    ILaunchConfiguration config = mock(ILaunchConfiguration.class);
    try {
      given(config.getType()).willReturn(type);
    } catch (CoreException e) {
      throw new AssertionError(e);
    }

    ILaunch launch = mock(ILaunch.class);
    given(launch.getLaunchConfiguration()).willReturn(config);
    return launch;
  }

  @Test public void returnsTheFiles() {
    Set<IPath> files = setOfFilePaths("/a/b", "/c/d");
    assertThat(eventWith(files).files(), is(files));
  }

  @Test public void returnsTheLaunchEtc() throws CoreException {
    ILaunch l = mockLaunch();
    LaunchEvent e = eventWith(l);
    assertThat(e.launch(), is(l));
    assertThat(e.launchConfig(), is(l.getLaunchConfiguration()));
    assertThat(e.launchConfigType(), is(l.getLaunchConfiguration().getType()));
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
    ILaunch launch = mockLaunch();
    given(launch.getLaunchConfiguration()).willReturn(null);
    eventWith(launch);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutLaunchConfigType() throws Exception {
    ILaunch launch = mockLaunch();
    given(launch.getLaunchConfiguration().getType()).willReturn(null);
    eventWith(launch);
  }

  @Override protected LaunchEvent newEvent(Instant instant, Duration duration) {
    return eventWith(instant, duration, mockLaunch(), NO_FILES);
  }

  static Set<IPath> setOfFilePaths(String... paths) {
    Set<IPath> set = newHashSet();
    for (String path : paths)
      set.add(new Path(path));
    return set;
  }

}
