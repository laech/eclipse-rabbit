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

import scala.math.Ordering

/** An object whose internal content can be ordered. */
trait Sortable {

  private var _ordering: Ordering[Any] = _

  /** The ordering of this instance's content, may be `null` if content is not
    * currently ordered.
    */
  def ordering = _ordering

  def ordering_=(ordering: Ordering[Any]) = _ordering = ordering
}