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
import static org.joda.time.Instant.now;
import static rabbit.tracking.tests.Instants.epoch;
import static rabbit.workbench.tracking.event.CommandEventTest.eventWith;
import static rabbit.workbench.tracking.event.CommandEventTest.newExecution;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rabbit.tracking.tests.EqualsTestBase;

@RunWith(Parameterized.class)
public final class CommandEventEqualsTest extends EqualsTestBase {

  private static ExecutionEvent EX = newExecution();

  @Parameters public static List<Object[]> data() {
    return asList(new Object[][]{
        {
            eventWith(epoch(), EX),
            eventWith(epoch(), EX),
            eventWith(now(), EX)},
        {
            eventWith(epoch(), EX),
            eventWith(epoch(), EX),
            eventWith(epoch(), newExecution())},
        {
            eventWith(epoch(), EX),
            eventWith(epoch(), EX),
            eventWith(now(), newExecution())}
    });
  }

  public CommandEventEqualsTest(
      Object a,
      Object objectEqualToA,
      Object objectNotEqualToA) {
    super(a, objectEqualToA, objectNotEqualToA);
  }
}
