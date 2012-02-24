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

package rabbit.tracking.util;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.util.Recorder.IRecorderListener;
import rabbit.tracking.util.Recorder.Record;

public final class RecorderTest {

  private static final class Listener implements IRecorderListener<Object> {

    Record<Object> record;
    int notificationCount;

    @Override public void onRecord(Record<Object> record) {
      this.record = record;
      this.notificationCount++;
    }

  }

  private Recorder<Object> recorder;

  @Before public void before() {
    recorder = Recorder.create();
  }

  @Test(expected = NullPointerException.class)//
  public void constructRecordWithoutDurationWillGetException() {
    Record.create(Instant.now(), null, "123");
  }

  @Test(expected = NullPointerException.class)//
  public void constructRecordWithoutStartTimeWillGetException() {
    Record.create(null, new Duration(10), "123");
  }

  @Test public void constructRecordWithoutUserDataIsOk() {
    Record.create(Instant.now(), new Duration(10), null);
  }

  @Test public void doesNotNotifyLisetnerIfRemoved() {
    Listener listener = new Listener();
    recorder.addListener(listener);
    recorder.removeListener(listener);
    recorder.start(null);
    recorder.stop();
    assertThat(listener.notificationCount, is(0));
  }

  @Test public void multiCallsToStartSameDataHasNoAffect() throws Exception {
    Listener listener = new Listener();
    recorder.addListener(listener);

    long start1 = currentTimeMillis();
    recorder.start("1");
    sleep(5);
    long start2 = currentTimeMillis();
    recorder.start("1");
    recorder.start("1");
    assertThat(listener.notificationCount, is(0));

    recorder.stop();
    long end = currentTimeMillis();
    assertThat(listener.notificationCount, is(1));
    check(listener.record, start1, end, "1");
    assertThat(listener.record.getStart().getMillis(), lessThan(start2));
  }

  @Test public void newSessionCanBeStartedWithoutStopping() throws Exception {
    Listener listener = new Listener();
    recorder.addListener(listener);

    recorder.start(null);
    sleep(5);
    long start = System.currentTimeMillis();
    recorder.start("aa");
    recorder.stop();
    long end = System.currentTimeMillis();

    assertThat(listener.notificationCount, is(2));
    check(listener.record, start, end, "aa");
  }

  @Test public void recordsWithCorrectProperties() throws Exception {
    Listener listener = new Listener();
    recorder.addListener(listener);

    long start = currentTimeMillis();
    recorder.start("123");
    sleep(5);
    recorder.stop();
    long end = currentTimeMillis();

    assertThat(listener.notificationCount, is(1));
    check(listener.record, start, end, "123");
  }

  @Test public void startArgumentIsOptional() {
    recorder.start(null);
  }

  @Test public void startOnNewDataWillStopPreviousSession() throws Exception {
    Listener listener = new Listener();
    recorder.addListener(listener);

    long start = currentTimeMillis();
    recorder.start("123");
    sleep(5);
    recorder.start("abc");
    long end = currentTimeMillis();

    assertThat(listener.notificationCount, is(1));
    check(listener.record, start, end, "123");
  }

  @Test public void stopHasNoAffectIfNotPreviouslyStarted() {
    Listener listener = new Listener();
    recorder.addListener(listener);
    recorder.stop();
    recorder.stop();
    recorder.stop();
    recorder.stop();
    assertThat(listener.notificationCount, is(0));
  }

  @Test(expected = NullPointerException.class)//
  public void throwsExceptionIfAddingANullListener() {
    recorder.addListener(null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsExceptionIfRemovingANullListener() {
    recorder.removeListener(null);
  }

  private void check(Record<?> r, long preStart, long postEnd, Object data) {
    assertThat(r, is(notNullValue()));
    assertThat(r.getData(), is(data));
    long start = r.getStart().getMillis();
    long end = start + r.getDuration().getMillis();
    assertThat(start, is(greaterThanOrEqualTo(preStart)));
    assertThat(end, is(lessThanOrEqualTo(postEnd)));
  }
}
