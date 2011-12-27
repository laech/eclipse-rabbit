/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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
package rabbit.ui.internal.viewers

abstract class ForwardingTest {

  trait Forwarding {

    /** The real instance wrapped by this forwarding object.
      *
      * This instance is created with `Mockito.mock`, which can be used with
      * `Mockito.verify` to verify that this forwarding object has forwarded
      * the correct calls to the mocked instance.
      */
    val delegate: Any
  }

  /** Creates an instance for testing. */
  protected def create: Forwarding
}