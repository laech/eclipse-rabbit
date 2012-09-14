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
import static rabbit.workbench.tracking.event.LaunchEventTest.eventWith;
import static rabbit.workbench.tracking.event.LaunchEventTest.mockLaunch;
import static rabbit.workbench.tracking.event.LaunchEventTest.setOfFilePaths;

import java.util.List;

import org.eclipse.debug.core.ILaunch;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rabbit.tracking.tests.EqualsTestBase;

@RunWith(Parameterized.class)
public final class LaunchEventEqualsTest extends EqualsTestBase {

  private static final ILaunch LAUNCH = mockLaunch();

  @Parameters public static List<Object[]> data() {
    return asList(new Object[][]{
        {
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(now(), ZERO, LAUNCH, NO_FILES)},
        {
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), millis(10), LAUNCH, NO_FILES)},
        {
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, mockLaunch(), NO_FILES)},
        {
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, setOfFilePaths("/a/b", "/c/d"))},
        {
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(epoch(), ZERO, LAUNCH, NO_FILES),
            eventWith(now(), millis(10), mockLaunch(), setOfFilePaths("/a/b"))},
    });
  }

  public LaunchEventEqualsTest(
      Object a,
      Object objectEqualToA,
      Object objectNotEqualToA) {
    super(a, objectEqualToA, objectNotEqualToA);
  }

}
