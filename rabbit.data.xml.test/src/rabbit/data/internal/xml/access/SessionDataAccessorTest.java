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
package rabbit.data.internal.xml.access;

import static rabbit.data.access.model.ISessionData.DATE;
import static rabbit.data.access.model.ISessionData.DURATION;
import static rabbit.data.access.model.ISessionData.WORKSPACE;
import static rabbit.data.internal.xml.DataStore.SESSION_STORE;

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.IntervalType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import static com.google.common.collect.Collections2.transform;

import com.google.common.base.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see SessionDataAccessor
 */
public class SessionDataAccessorTest extends
    AbstractAccessorTest2<ISessionData, SessionEventType, SessionEventListType> {

  @Override
  protected void assertValues(SessionEventType expected,
      LocalDate expectedDate, WorkspaceStorage expectedWs, ISessionData actual) {
    assertThat(actual.get(DATE), is(expectedDate));
    assertThat(actual.get(WORKSPACE), is(expectedWs));
    assertThat(actual.get(DURATION).getMillis(), is(expected.getDuration()));

    List<Interval> actualIntervals = actual.get(ISessionData.INTERVALS);
    List<IntervalType> types = expected.getInterval();
    Interval[] expectedIntervals = transform(types,
        new Function<IntervalType, Interval>() {
          @Override
          public Interval apply(IntervalType type) {
            return new Interval(
                type.getStartTime(),
                type.getStartTime() + type.getDuration());
          }
        }).toArray(new Interval[types.size()]);
    assertThat(types.size(), is(not(0)));
    assertThat(actualIntervals.size(), is(types.size()));
    assertThat(actualIntervals, hasItems(expectedIntervals));
  }

  @Override
  protected SessionDataAccessor create() {
    return new SessionDataAccessor(SESSION_STORE);
  }

  @Override
  protected SessionEventListType createCategory() {
    final SessionEventListType list = new SessionEventListType();
    list.setDate(DatatypeUtil.toXmlDate(new LocalDate()));
    return list;
  }

  @Override
  protected SessionEventType createElement() {
    final IntervalType interval1 = new IntervalType();
    interval1.setDuration(1000);
    interval1.setStartTime(10);
    final IntervalType interval2 = new IntervalType();
    interval2.setDuration(2000);
    interval2.setStartTime(20);
    final SessionEventType type = new SessionEventType();
    type.setDuration(19834);
    type.getInterval().add(interval1);
    type.getInterval().add(interval2);
    return type;
  }

  @Override
  protected List<SessionEventListType> getCategories(EventListType events) {
    return events.getSessionEvents();
  }

  @Override
  protected List<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }

}
