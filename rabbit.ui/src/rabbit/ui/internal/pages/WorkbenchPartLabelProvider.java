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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPartDescriptor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Label provider for {@link IWorkbenchPartDescriptor}
 */
public class WorkbenchPartLabelProvider extends BaseLabelProvider implements
    ILabelProvider {

  /** Keys are workbench part id, values may be null. */
  private Map<String, Image> images;

  /**
   * Constructs a new page.
   * 
   * @param page The parent.
   */
  public WorkbenchPartLabelProvider() {
    images = new HashMap<String, Image>();
  }

  @Override
  public void dispose() {
    super.dispose();
    for (Image img : images.values()) {
      if (img != null)
        img.dispose();
    }
  }

  @Override
  public Image getImage(@Nullable Object element) {
    if (element instanceof IWorkbenchPartDescriptor) {
      IWorkbenchPartDescriptor part = (IWorkbenchPartDescriptor) element;
      if (images.containsKey(part.getId())) {
        return images.get(part.getId());
      } else {
        ImageDescriptor des = part.getImageDescriptor();
        Image img = (des != null) ? des.createImage() : null;
        images.put(part.getId(), img);
        return img;
      }
    } else {
      return null;
    }
  }

  @Override
  public String getText(@Nullable Object element) {
    if (element instanceof IWorkbenchPartDescriptor)
      return ((IWorkbenchPartDescriptor) element).getLabel();
    else
      return null;
  }
}
