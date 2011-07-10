/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.merge;

import rabbit.data.internal.xml.schema.events.IntervalType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see SessionEventTypeMerger
 */
public class SessionEventTypeMergerTest extends
    AbstractMergerTest<SessionEventType> {

  @Override
  protected AbstractMerger<SessionEventType> createMerger() {
    return new SessionEventTypeMerger();
  }

  @Override
  protected SessionEventType createTargetType() {
    final SessionEventType type = new SessionEventType();
    type.setDuration(109);
    final IntervalType interval = new IntervalType();
    interval.setStartTime(100);
    interval.setDuration(1000);
    type.getInterval().add(interval);
    return type;
  }

  @Override
  protected SessionEventType createTargetTypeDiff() {
    return null;
  }

  @Test
  @Override
  @Ignore("Does not apply, all SessionEventTypes are mergeable")
  public void testMerger_withNotMergeableParam() {
    super.testMerger_withNotMergeableParam();
  }

  @Override
  public void testIsMergeable() throws Exception {
    assertTrue(merger.isMergeable(
        new SessionEventType(),
        new SessionEventType()));
  }

  @Override
  public void testMerge() throws Exception {
    final int duration1 = 9823;
    final int duration2 = 120934;

    final IntervalType interval1 = new IntervalType();
    interval1.setDuration(100);
    interval1.setStartTime(1000);

    final IntervalType interval2 = new IntervalType();
    interval2.setDuration(102);
    interval2.setStartTime(2000);

    final IntervalType interval3 = new IntervalType();
    interval3.setDuration(1);
    interval3.setStartTime(1020);

    final SessionEventType t1 = new SessionEventType();
    t1.setDuration(duration1);
    t1.getInterval().add(interval1);
    t1.getInterval().add(interval2);
    final SessionEventType t2 = new SessionEventType();
    t2.setDuration(duration2);
    t2.getInterval().add(interval3);

    final SessionEventType result = merger.merge(t1, t2);
    assertEquals(duration1 + duration2, result.getDuration());
    assertTrue(result.getInterval().containsAll(
        Arrays.asList(interval1, interval2, interval3)));
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    final int duration1 = 98123;
    final int duration2 = 12934;
    final int intervalStart1 = 1000;
    final int intervalStart2 = 10000;
    final int intervalDuration1 = 200;
    final int intervalDuration2 = 1001;

    final IntervalType interval1 = new IntervalType();
    interval1.setDuration(intervalDuration1);
    interval1.setStartTime(intervalStart1);

    final IntervalType interval2 = new IntervalType();
    interval2.setDuration(intervalDuration2);
    interval2.setStartTime(intervalStart2);

    final SessionEventType t1 = new SessionEventType();
    t1.setDuration(duration1);
    t1.getInterval().add(interval1);
    final SessionEventType t2 = new SessionEventType();
    t2.setDuration(duration2);
    t2.getInterval().add(interval2);

    final SessionEventType result = merger.merge(t1, t2);
    assertNotSame(t1, result);
    assertNotSame(t2, result);
    assertEquals(duration1, t1.getDuration());
    assertEquals(duration2, t2.getDuration());
    assertEquals(1, t1.getInterval().size());
    assertEquals(intervalDuration1, t1.getInterval().get(0).getDuration());
    assertEquals(intervalStart1, t1.getInterval().get(0).getStartTime());
    assertEquals(1, t1.getInterval().size());
    assertEquals(intervalDuration2, t2.getInterval().get(0).getDuration());
    assertEquals(intervalStart2, t2.getInterval().get(0).getStartTime());
  }
}
