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

package rabbit.tracking.tests

import java.util.concurrent.CountDownLatch

import rabbit.tracking.tests.TestImplicits.funToRunnable

object TestUtils {

  /**
   * Performs actual in new threads and wait for all of them to finish before
   * returning.
   *
   * @param n the number of threads to use
   * @param f the code to execute in new thread
   */
  def doInNewThreads(n: Int, f: => Unit) {
    var error: Throwable = null
    val startSignal = new CountDownLatch(1)
    val doneSignal = new CountDownLatch(n)
    (0 until n) foreach { _ =>
      new Thread(() => {
        startSignal.await()
        try {
          f
        } catch {
          case t: Throwable => error = t
        }
        doneSignal.countDown()
      }).start()
    }
    startSignal.countDown()
    doneSignal.await()
    if (error != null) {
      throw error
    }
  }
}