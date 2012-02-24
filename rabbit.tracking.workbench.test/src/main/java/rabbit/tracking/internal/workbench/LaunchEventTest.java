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
package rabbit.tracking.internal.workbench;

import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.core.runtime.Path.fromPortableString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.joda.time.Duration.millis;
import static org.joda.time.Instant.now;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static rabbit.tracking.internal.workbench.LaunchEvent.fromLaunch;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.TimedEventTest;

import com.google.common.collect.ImmutableSet;

public final class LaunchEventTest extends TimedEventTest {

  private Instant instant;
  private Duration duration;
  private ILaunch launch;
  private ILaunchConfiguration config;
  private ILaunchConfigurationType type;
  private Set<IPath> paths;

  private LaunchEvent event;

  @Before public void init() {
    instant = now();
    duration = millis(100);
    launch = mock(ILaunch.class);
    config = mock(ILaunchConfiguration.class);
    type = mock(ILaunchConfigurationType.class);
    paths = ImmutableSet.<IPath> of(new Path("/a/b"), new Path("/d/e"));
    event = create(instant, duration, paths, launch, config, type);
  }

  @Test public void constructorMakesACopyOfTheFilePaths() {
    paths = newHashSet(paths);
    event = create(instant, duration, paths, launch, config, type);
    paths.add(new Path("/Should/not/effect/the/collection/in/the/event"));
    assertThat(event.files(), not(equalTo(paths)));
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfFilePathsIsNull() {
    create(instant, duration, null, launch, config, type);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfLaunchConfigurationIsNull() {
    create(instant, duration, paths, launch, null, type);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfLaunchConfigurationTypeIsNull() {
    create(instant, duration, paths, launch, config, null);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfLaunchIsNull() {
    create(instant, duration, paths, null, config, type);
  }

  @Test public void fromLaunchThrowsExceptionIfCantGetLaunchConfiguration() {
    given(launch.getLaunchConfiguration()).willReturn(null);
    try {
      fromLaunch(instant, duration, paths, launch);
      fail();
    } catch (RuntimeException e) {
      // Pass
    }
  }

  @Test public void fromLaunchThrowsExceptionIfCantGetLaunchConfigurationType() {
    try {
      given(launch.getLaunchConfiguration()).willReturn(config);
      given(config.getType()).willReturn(null);
      try {
        fromLaunch(instant, duration, paths, launch);
        fail();
      } catch (RuntimeException e) {
        // Pass
      }
    } catch (CoreException e) {
      throw new AssertionError("How did this happen?");
    }
  }

  @Test public void returnsTheLaunch() {
    assertThat(event.launch(), is(launch));
  }

  @Test public void returnsTheLaunchConfiguration() {
    assertThat(event.launchConfig(), is(config));
  }

  @Test public void returnsTheLaunchConfigurationType() {
    assertThat(event.launchConfigType(), is(type));
  }

  @Test public void returnsThePaths() {
    assertThat(event.files(), is(paths));
  }

  @Test(expected = UnsupportedOperationException.class)//
  public void returnsThePathsAsUnmodifiableCollection() {
    event.files().add(fromPortableString("/Should/throw/exception"));
  }

  @Override protected final LaunchEvent create(Instant instant,
      Duration duration) {
    return create(instant, duration, paths, launch, config, type);
  }

  private LaunchEvent create(
      Instant instant,
      Duration duration,
      Set<? extends IPath> filePaths,
      ILaunch launch,
      ILaunchConfiguration config,
      ILaunchConfigurationType type) {
    return new LaunchEvent(instant, duration, filePaths, launch, config, type);
  }
}