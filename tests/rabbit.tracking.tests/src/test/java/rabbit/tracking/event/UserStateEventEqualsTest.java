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

package rabbit.tracking.event;

import static java.util.Arrays.asList;
import static org.joda.time.Instant.now;
import static rabbit.tracking.event.UserStateEventTest.create;
import static rabbit.tracking.tests.Instants.epoch;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rabbit.tracking.tests.EqualsTestBase;

@RunWith(Parameterized.class)
public final class UserStateEventEqualsTest extends EqualsTestBase {

  @Parameters public static List<Object[]> data() {
    return asList(new Object[][]{
        {
            create(epoch()),
            create(epoch()),
            create(now())},
        {
            create(true),
            create(true),
            create(false)},
        {
            create(epoch(), true),
            create(epoch(), true),
            create(now(), false)},
    });
  }

  public UserStateEventEqualsTest(Object a1, Object a2, Object b) {
    super(a1, a2, b);
  }

}
