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

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

/**
 * @see SessionData
 */
public class SessionDataTest {

  private final LocalDate date = new LocalDate();
  private final LocalTime time = new LocalTime();
  private final Duration duration = new Duration(11);
  private final WorkspaceStorage workspace = new WorkspaceStorage(
      new Path("/a"), new Path("/b"));

  private final SessionData data = create(date, workspace, duration, time);

  @Test
  public void shouldReturnTheDate() {
    assertThat(data.get(ISessionData.DATE), is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(data.get(ISessionData.DURATION), is(duration));
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(data.get(null), is(nullValue()));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(data.get(ISessionData.WORKSPACE), is(workspace));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, time);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, time);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, time);
  }

  @Test
  public void shouldAllowToBeConstructedWithANullTime() throws Exception {
    /*
     * Time is allowed to be null because data don't have "time" before 1.3
     */
    create(date, workspace, duration, null);
    // No exception
  }

  /**
   * @see SessionData#SessionData(LocalDate, WorkspaceStorage, Duration)
   */
  private SessionData create(LocalDate date, WorkspaceStorage workspace,
      Duration duration, LocalTime time) {
    return new SessionData(date, workspace, duration);
  }
}
