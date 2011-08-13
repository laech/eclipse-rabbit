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
package rabbit.data.internal.access.model;

import static rabbit.data.access.model.ISessionData.DATE;
import static rabbit.data.access.model.ISessionData.DURATION;
import static rabbit.data.access.model.ISessionData.INTERVALS;
import static rabbit.data.access.model.ISessionData.WORKSPACE;

import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

/**
 * @see SessionData
 */
public class SessionDataTest {

  final LocalDate date = new LocalDate();
  final Duration duration = new Duration(11);
  final List<Interval> intervals = asList(new Interval(10, 100));
  final WorkspaceStorage workspace = new WorkspaceStorage(
      new Path("/a"), new Path("/b"));

  final SessionData data = create(date, workspace, duration, intervals);

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(data.get(null), is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(data.get(DATE), is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(data.get(DURATION), is(duration));
  }

  @Test
  public void shouldReturnTheIntervals() {
    assertThat(data.get(INTERVALS), is(intervals));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldReturnTheIntervalsAsAnImmutableCollection() {
    create(date, workspace, duration, new ArrayList<Interval>())
        .get(INTERVALS).add(new Interval(1, 2));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(data.get(WORKSPACE), is(workspace));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, intervals);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, intervals);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, intervals);
  }

  /**
   * @see SessionData#SessionData(LocalDate, WorkspaceStorage, Duration, List)
   */
  private SessionData create(LocalDate date, WorkspaceStorage workspace,
      Duration duration, List<Interval> intervals) {
    return new SessionData(date, workspace, duration, intervals);
  }
}
