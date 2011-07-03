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

import rabbit.data.internal.xml.schema.events.SessionEventType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

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
    SessionEventType type = new SessionEventType();
    type.setDuration(109);
    type.setIntervalArray("100:100");
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
    int duration1 = 9823;
    int duration2 = 120934;
    SessionEventType t1 = new SessionEventType();
    t1.setDuration(duration1);
    t1.setIntervalArray("100:200;400:100");
    SessionEventType t2 = new SessionEventType();
    t2.setDuration(duration2);
    t2.setIntervalArray("400:1");

    SessionEventType result = merger.merge(t1, t2);
    assertEquals(duration1 + duration2, result.getDuration());
    assertEquals(t1.getIntervalArray() + ";" + t2.getIntervalArray(),
        result.getIntervalArray());
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    final int duration1 = 98123;
    final int duration2 = 12934;
    final String interval1 = "100:200";
    final String interval2 = "200:300;500:600";
    final SessionEventType t1 = new SessionEventType();
    t1.setDuration(duration1);
    t1.setIntervalArray(interval1);
    final SessionEventType t2 = new SessionEventType();
    t2.setDuration(duration2);
    t2.setIntervalArray(interval2);

    final SessionEventType result = merger.merge(t1, t2);
    assertNotSame(t1, result);
    assertNotSame(t2, result);
    assertEquals(duration1, t1.getDuration());
    assertEquals(duration2, t2.getDuration());
    assertEquals(interval1, t1.getIntervalArray());
    assertEquals(interval2, t2.getIntervalArray());
  }
}
