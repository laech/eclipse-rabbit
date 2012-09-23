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

import static java.util.Arrays.asList;
import static org.joda.time.Duration.ZERO;
import static org.joda.time.Duration.millis;
import static org.joda.time.Instant.now;
import static rabbit.tracking.tests.Instants.epoch;
import static rabbit.workbench.tracking.event.LaunchEventTest.NO_FILES;
import static rabbit.workbench.tracking.event.LaunchEventTest.mockLaunch;
import static rabbit.workbench.tracking.event.LaunchEventTest.mockLaunchConfig;
import static rabbit.workbench.tracking.event.LaunchEventTest.mockLaunchConfigType;
import static rabbit.workbench.tracking.event.LaunchEventTest.setOfFilePaths;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rabbit.tracking.tests.EqualsTestBase;

@RunWith(Parameterized.class)
public final class LaunchEventEqualsTest extends EqualsTestBase {

  private static final ILaunch LAUNCH = mockLaunch();
  private static final ILaunchConfiguration CONFIG = mockLaunchConfig();
  private static final ILaunchConfigurationType TYPE = mockLaunchConfigType();

  @Parameters public static List<Object[]> data() {
    LaunchEvent event = new LaunchEvent(
        epoch(), ZERO, LAUNCH, CONFIG, TYPE, NO_FILES);

    return asList(new Object[][]{
        {
            event,
            clone(event),
            eventWith(event, now())},
        {
            event,
            clone(event),
            eventWith(event, millis(10))},
        {
            event,
            clone(event),
            eventWith(event, mockLaunch())},
        {
            event,
            clone(event),
            eventWith(event, mockLaunchConfig())},
        {
            event,
            clone(event),
            eventWith(event, mockLaunchConfigType())},
        {
            event,
            clone(event),
            eventWith(event, setOfFilePaths("/a/b", "/c/d"))},
        {
            event,
            clone(event),
            new LaunchEvent(
                now(),
                millis(10),
                mockLaunch(),
                mockLaunchConfig(),
                mockLaunchConfigType(),
                setOfFilePaths("/a/b"))},
    });
  }

  private static LaunchEvent clone(LaunchEvent that) {
    return new LaunchEvent(
        that.instant(),
        that.duration(),
        that.launch(),
        that.launchConfig(),
        that.launchConfigType(),
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that, Instant instant) {
    return new LaunchEvent(
        instant,
        that.duration(),
        that.launch(),
        that.launchConfig(),
        that.launchConfigType(),
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that, Duration duration) {
    return new LaunchEvent(
        that.instant(),
        duration,
        that.launch(),
        that.launchConfig(),
        that.launchConfigType(),
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that, ILaunch launch) {
    return new LaunchEvent(
        that.instant(),
        that.duration(),
        launch,
        that.launchConfig(),
        that.launchConfigType(),
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that,
      ILaunchConfiguration config) {
    return new LaunchEvent(
        that.instant(),
        that.duration(),
        that.launch(),
        config,
        that.launchConfigType(),
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that,
      ILaunchConfigurationType type) {
    return new LaunchEvent(
        that.instant(),
        that.duration(),
        that.launch(),
        that.launchConfig(),
        type,
        that.files());
  }

  private static LaunchEvent eventWith(LaunchEvent that, Set<IPath> files) {
    return new LaunchEvent(
        that.instant(),
        that.duration(),
        that.launch(),
        that.launchConfig(),
        that.launchConfigType(),
        files);
  }

  public LaunchEventEqualsTest(
      Object a,
      Object objectEqualToA,
      Object objectNotEqualToA) {
    super(a, objectEqualToA, objectNotEqualToA);
  }

}
