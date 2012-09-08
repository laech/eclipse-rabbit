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

package rabbit.tracking;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static rabbit.tracking.tests.Threads.runInNewThreads;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import rabbit.tracking.AbstractTrackerTest.Tester;

public final class AbstractTrackerTest extends AbstractTrackerTestBase<Tester> {

  private static interface IAction {
    void call(Tester tracker);
  }

  private static interface IVerifier {
    void verify(Tester tracker);
  }

  static class Tester extends AbstractTracker {
    private final AtomicInteger startCount = new AtomicInteger();
    private final AtomicInteger stopCount = new AtomicInteger();

    public int startCount() {
      return startCount.get();
    }

    public int stopCount() {
      return stopCount.get();
    }

    @Override protected void onStart() {
      startCount.incrementAndGet();
    }

    @Override protected void onStop() {
      stopCount.incrementAndGet();
    }
  }

  @Test public void notifiesOnStart() {
    tracker().stop();
    tracker().start();
    assertThat(tracker().startCount(), is(1));
  }

  @Test public void notifiesOnStartOnlyIfWasStopped() {
    tracker().stop();
    tracker().start();
    tracker().start();
    assertThat(tracker().startCount(), is(1));
  }

  @Test public void notifiesOnStop() {
    tracker().start();
    tracker().stop();
    assertThat(tracker().stopCount(), is(1));
  }

  @Test public void notifiesOnStopOnlyIfWasStarted() {
    tracker().start();
    tracker().stop();
    tracker().stop();
    assertThat(tracker().stopCount(), is(1));
  }

  @Test public void handlesStartingConcurrently() {
    runConcurrently(new IAction() {
      @Override public void call(Tester tracker) {
        tracker.start();
      }
    }, new IVerifier() {
      @Override public void verify(Tester tracker) {
        assertThat(tracker.startCount(), is(1));
        assertThat(tracker.isStarted(), is(true));
      }
    });
  }

  @Test public void handlesStoppingConcurrently() {
    runConcurrently(new IAction() {
      @Override public void call(Tester tracker) {
        tracker.stop();
      }
    }, new IVerifier() {
      @Override public void verify(Tester tracker) {
        assertThat(tracker.stopCount(), is(1));
        assertThat(tracker.isStarted(), is(false));
      }
    });
  }

  @Test public void handlesStartingStoppingConcurrently() {
    runConcurrently(new IAction() {
      @Override public void call(Tester tracker) {
        tracker.start();
        tracker.stop();
      }
    }, new IVerifier() {
      @SuppressWarnings("unchecked")//
      @Override public void verify(Tester tracker) {
        int diff = tracker.startCount() - tracker.stopCount();
        assertThat(diff, anyOf(is(0), is(1), is(-1)));
        assertThat(tracker.isStarted(), is(diff > 0));
      }
    });
  }

  @Override protected Tester newTracker() {
    return new Tester();
  }

  private void runConcurrently(final IAction action, final IVerifier verifier) {
    runInNewThreads(100, new Runnable() {
      @Override public void run() {
        final Tester tracker = newTracker();
        tracker.start();
        runInNewThreads(20, new Runnable() {
          @Override public void run() {
            for (int i = 0; i < 100; ++i)
              action.call(tracker);
          }
        });
        verifier.verify(tracker);
      }
    });
  }
}
