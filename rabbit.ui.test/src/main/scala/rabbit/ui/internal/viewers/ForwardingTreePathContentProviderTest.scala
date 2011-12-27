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

import org.eclipse.jface.viewers.ITreePathContentProvider
import org.eclipse.jface.viewers.TreePath
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

class ForwardingTreePathContentProviderTest
  extends ForwardingStructuredContentProviderTest {

  @Test def getChildrenShouldBeDelegated {
    val parent = TreePath.EMPTY
    val obj = create
    obj.getChildren(parent)
    verify(obj.delegate, only).getChildren(parent)
  }

  @Test def getParentsShouldBeDelegated {
    val child = new Object
    val obj = create
    obj.getParents(child)
    verify(obj.delegate, only).getParents(child)
  }

  @Test def hasChildrenShouldBeDelegated {
    val parent = TreePath.EMPTY
    val obj = create
    obj.hasChildren(parent)
    verify(obj.delegate, only).hasChildren(parent)
  }

  override protected def create =
    new ForwardingTreePathContentProvider with Forwarding {
      override val delegate = mock(classOf[ITreePathContentProvider])
    }
}