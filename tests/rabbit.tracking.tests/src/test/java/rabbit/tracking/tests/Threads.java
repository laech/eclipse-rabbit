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

package rabbit.tracking.tests;

import java.util.concurrent.CountDownLatch;

public final class Threads {

  /**
   * Runs the given code in new threads and wait for all of them to finish
   * before returning.
   * 
   * @param threads the number of threads to use
   * @param code the code to execute in new thread
   */
  public static void runInNewThreads(int threads, final Runnable code) {
    final Exception[] error = new Exception[1];
    final CountDownLatch start = new CountDownLatch(1);
    final CountDownLatch done = new CountDownLatch(threads);
    for (int i = 0; i < threads; ++i)
      startThread(code, error, start, done);

    start.countDown();
    try {
      done.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (error[0] != null)
      throw new RuntimeException(error[0]);
  }

  private static void startThread(
      final Runnable code,
      final Exception[] errorHolder,
      final CountDownLatch start,
      final CountDownLatch done) {

    new Thread() {
      @Override public void run() {
        try {
          start.await();
          code.run();
        } catch (Exception e) {
          errorHolder[0] = e;
        } finally {
          done.countDown();
        }
      }
    }.start();
  }

  private Threads() {
  }
}
