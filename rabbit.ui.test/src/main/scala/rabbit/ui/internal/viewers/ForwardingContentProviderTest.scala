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

import org.eclipse.jface.viewers.IContentProvider
import org.eclipse.jface.viewers.Viewer
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

class ForwardingContentProviderTest extends ForwardingTest {

  @Test def disposeShouldBeDelegated {
    val obj = create
    obj.dispose
    verify(obj.delegate, only).dispose
  }

  @Test def inputChangedShouldBeDelegated {
    val viewer: Viewer = null
    val oldInput = new Object
    val newInput = new Object
    
    val obj = create
    obj.inputChanged(viewer, oldInput, newInput)
    verify(obj.delegate, only).inputChanged(viewer, oldInput, newInput)
  }

  override protected def create = new ForwardingContentProvider with Forwarding {
    override val delegate = mock(classOf[IContentProvider])
  }
}