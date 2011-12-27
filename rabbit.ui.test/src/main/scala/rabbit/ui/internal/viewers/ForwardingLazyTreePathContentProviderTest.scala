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

import org.eclipse.jface.viewers.ILazyTreePathContentProvider
import org.eclipse.jface.viewers.TreePath
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

final class ForwardingLazyTreePathContentProviderTest
  extends ForwardingContentProviderTest {

  @Test def getParentsShouldBeDelegated {
    val element = "elem"
    val obj = create
    obj.getParents(element)
    verify(obj.delegate, only).getParents(element)
  }

  @Test def updateChildCountShouldBeDelegated {
    val path = new TreePath(Array("1", "2"))
    val currentChildCount = 19
    val obj = create
    obj.updateChildCount(path, currentChildCount)
    verify(obj.delegate, only).updateChildCount(path, currentChildCount)
  }

  @Test def updateElementShouldBeDelegated {
    val path = new TreePath(Array("1", "2", "4"))
    val index = 11
    val obj = create
    obj.updateElement(path, index)
    verify(obj.delegate, only).updateElement(path, index)
  }

  @Test def updateHasChildrenShouldBeDelegated {
    val path = new TreePath(Array("1", "7"))
    val obj = create
    obj.updateHasChildren(path)
    verify(obj.delegate, only).updateHasChildren(path)
  }

  override protected def create =
    new ForwardingLazyTreePathContentProvider with Forwarding {
      override val delegate = mock(classOf[ILazyTreePathContentProvider])
    }
}