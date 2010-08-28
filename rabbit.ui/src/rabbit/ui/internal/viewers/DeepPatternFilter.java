/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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
package rabbit.ui.internal.viewers;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A {@link PatternFilter} that is designed to be used with tree viewers that
 * have more than two levels.
 * 
 * This filter will select any elements in the tree that contains the pattern
 * regardless whether it's a parent or a leaf.
 */
public class DeepPatternFilter extends PatternFilter {

  @Override
  protected boolean isParentMatch(Viewer viewer, Object element) {

    if (isLeafMatch(viewer, element)) {
      return true;
    }

    Object[] children = ((ITreeContentProvider) ((AbstractTreeViewer) viewer)
        .getContentProvider()).getChildren(element);

    if (children != null) {
      for (Object child : children) {
        if (isParentMatch(viewer, child)) {
          return true;
        }
      }
    }
    return false;
  }

}
