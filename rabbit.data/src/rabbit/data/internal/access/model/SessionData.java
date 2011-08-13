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

import rabbit.data.access.model.IKey;
import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Contains session data information.
 */
public class SessionData implements ISessionData {

  /**
   * Immutable map of data.
   */
  private final Map<IKey<? extends Object>, Object> data;

  /**
   * Constructor.
   * 
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param duration The duration of the session.
   * @throws NullPointerException If any argument is {@code null}
   */
  public SessionData(
      LocalDate date,
      WorkspaceStorage workspace,
      Duration duration) {
    this(date, workspace, duration, ImmutableList.<Interval> of());
  }

  /**
   * Constructor.
   * 
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param duration The duration of the session.
   * @param intervals The intervals representing the active durations of the
   *        session.
   * @throws NullPointerException If {@code date}, {@code duration},
   *         {@code workspace} is <code>null</code>.
   */
  public SessionData(
      LocalDate date,
      WorkspaceStorage workspace,
      Duration duration,
      List<Interval> intervals) {

    data = ImmutableMap.of(
        DATE, checkNotNull(date, "date"),
        DURATION, checkNotNull(duration, "duration"),
        WORKSPACE, checkNotNull(workspace, "workspace"),
        INTERVALS, ImmutableList.copyOf(checkNotNull(intervals, "intervals")));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(@Nullable IKey<T> key) {
    return (T)data.get(key);
  }
}
