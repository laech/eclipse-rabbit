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

import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.internal.xml.schema.events.IntervalType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Arrays;

/**
 * @see FileEventTypeMerger
 */
public class FileEventTypeMergerTest extends AbstractMergerTest<FileEventType> {

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on both
   * parameters,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_bothParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath(null);
    FileEventType t2 = new FileEventType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on the first
   * parameter, and returns not null on the second parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_firstParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath(null);
    FileEventType t2 = new FileEventType();
    t2.setFilePath("notNull");

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  /**
   * Tests when {@link FileEventType#getFilePath()} returns null on the second
   * parameter, and returns not null on the first parameter,
   * {@link FileEventTypeMerger#isMergeable(FileEventType, FileEventType)}
   * should return false instead of failing.
   */
  @Test
  public void testIsMerageable_secondParamGetFileIdReturnsNull() {
    FileEventType t1 = new FileEventType();
    t1.setFilePath("notNull");
    FileEventType t2 = new FileEventType();
    t2.setFilePath(null);

    try {
      assertFalse(merger.isMergeable(t1, t2));
    } catch (Exception e) {
      fail("Should return false instead of exception");
    }
  }

  @Override
  public void testIsMergeable() throws Exception {
    FileEventType t1 = createTargetType();
    FileEventType t2 = createTargetTypeDiff();

    assertTrue(merger.isMergeable(t1, t1));
    assertFalse(merger.isMergeable(t1, t2));

    t2.setFilePath(t1.getFilePath());
    assertTrue(merger.isMergeable(t1, t2));
  }

  @Override
  public void testMerge() throws Exception {
    final IntervalType interval1 = new IntervalType();
    interval1.setDuration(100);
    interval1.setStartTime(1000);

    final IntervalType interval2 = new IntervalType();
    interval2.setDuration(102);
    interval2.setStartTime(2000);

    final IntervalType interval3 = new IntervalType();
    interval3.setDuration(1);
    interval3.setStartTime(1020);

    final FileEventType t1 = createTargetType();
    t1.setDuration(100);
    t1.getInterval().add(interval1);

    final FileEventType t2 = createTargetTypeDiff();
    t2.setFilePath(t1.getFilePath());
    t2.setDuration(3000);
    t2.getInterval().add(interval2);
    t2.getInterval().add(interval3);

    final String fileId = t1.getFilePath();
    final long totalDuration = t1.getDuration() + t2.getDuration();

    final FileEventType result = merger.merge(t1, t2);
    assertEquals(fileId, result.getFilePath());
    assertEquals(totalDuration, result.getDuration());
    assertTrue(result.getInterval().containsAll(
        Arrays.asList(interval1, interval2, interval3)));
  }

  @Override
  public void testMerge_notModifyParams() throws Exception {
    final String fileId = "amAnCommandId";
    final int duration1 = 10010;
    final int duration2 = 187341;
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

    final FileEventType t1 = new FileEventType();
    t1.setFilePath(fileId);
    t1.setDuration(duration1);
    t1.getInterval().add(interval1);
    final FileEventType t2 = new FileEventType();
    t2.setFilePath(fileId);
    t2.setDuration(duration2);
    t2.getInterval().add(interval2);

    final FileEventType result = merger.merge(t1, t2);
    assertNotSame(t1, result);
    assertNotSame(t2, result);
    assertEquals(fileId, t1.getFilePath());
    assertEquals(duration1, t1.getDuration());
    assertEquals(fileId, t2.getFilePath());
    assertEquals(duration2, t2.getDuration());
    assertEquals(1, t1.getInterval().size());
    assertEquals(intervalDuration1, t1.getInterval().get(0).getDuration());
    assertEquals(intervalStart1, t1.getInterval().get(0).getStartTime());
    assertEquals(1, t1.getInterval().size());
    assertEquals(intervalDuration2, t2.getInterval().get(0).getDuration());
    assertEquals(intervalStart2, t2.getInterval().get(0).getStartTime());
  }

  @Override
  protected FileEventTypeMerger createMerger() {
    return new FileEventTypeMerger();
  }

  @Override
  protected FileEventType createTargetType() {
    FileEventType type = new FileEventType();
    type.setFilePath("afileId");
    type.setDuration(10);

    final IntervalType interval = new IntervalType();
    interval.setStartTime(100);
    interval.setDuration(1000);
    type.getInterval().add(interval);
    return type;
  }

  @Override
  protected FileEventType createTargetTypeDiff() {
    FileEventType type = new FileEventType();
    type.setFilePath("afileIdabcdefg123");
    type.setDuration(110);

    final IntervalType interval = new IntervalType();
    interval.setStartTime(200);
    interval.setDuration(2000);
    type.getInterval().add(interval);
    return type;
  }

}
