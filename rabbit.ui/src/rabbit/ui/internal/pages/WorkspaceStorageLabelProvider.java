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
package rabbit.ui.internal.pages;

import rabbit.data.access.model.WorkspaceStorage;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Provides labels for {@link WorkspaceStorage}.
 */
public final class WorkspaceStorageLabelProvider extends LabelProvider implements IColorProvider {
  
  private static final Color GRAY = PlatformUI.getWorkbench().getDisplay()
      .getSystemColor(SWT.COLOR_DARK_GRAY);

  public WorkspaceStorageLabelProvider() {
  }

  @Override
  public String getText(Object element) {
    if (element instanceof WorkspaceStorage) {
      WorkspaceStorage ws = (WorkspaceStorage) element;
      if (ws.getWorkspacePath() != null) {
        return ws.getWorkspacePath().lastSegment() + " (" + ws.getWorkspacePath().toOSString() + ")";
      } else {
        return "Unknown (may be " + ws.getStoragePath().lastSegment().replace(".", "/") + "?)";
      }
    }
    return super.getText(element);
  }

  @Override
  public Image getImage(Object element) {
    return super.getImage(element);
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof WorkspaceStorage) {
      if (((WorkspaceStorage) element).getWorkspacePath() == null) {
        return GRAY;
      }
    }
    return null;
  }

  @Override
  public Color getBackground(Object element) {
    return null;
  }
}
