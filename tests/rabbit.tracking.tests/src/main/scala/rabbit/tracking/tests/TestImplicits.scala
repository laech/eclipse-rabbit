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

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

object TestImplicits {

  implicit def funToAnswer(f: InvocationOnMock => Any): Answer[Unit] = new Answer[Unit] {
    override def answer(invocation: InvocationOnMock) = f(invocation)
  }

  implicit def funToRunnable(f: () => Any): Runnable = new Runnable {
    override def run = f()
  }

  implicit def nTimes(n: Int) = new {
    def times(f: => Unit) {
      for (_ <- 0 until n) f
    }
  }
}