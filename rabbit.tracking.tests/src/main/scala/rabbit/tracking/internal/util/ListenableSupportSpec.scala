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

package rabbit.tracking.internal.util

import java.util.concurrent.atomic.AtomicInteger

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import rabbit.tracking.ListenableSpecBase

@RunWith(classOf[JUnitRunner])
final class ListenableSupportSpec
  extends ListenableSpecBase[String, ListenableSupport[String]] {

  private val listenerCount = new AtomicInteger

  protected override val supportsCreateWithListeners = true

  protected override def newUniqueListener() = "listener #" + listenerCount.getAndIncrement()

  protected override def getListeners(listenable: ListenableSupport[String]) =
    listenable.getListeners()

  protected override def create() = createWithListeners()

  protected override def createWithListeners(listeners: String*) =
    ListenableSupport.create(listeners: _*)
}