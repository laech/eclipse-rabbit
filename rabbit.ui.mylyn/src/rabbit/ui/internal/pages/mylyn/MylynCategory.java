/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.pages.mylyn;

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Mylyn categories.
 */
public enum MylynCategory implements ICategory {

  /** File category */
  FILE    ("Files", PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FILE)),
  
  /** Folder category */
  FOLDER  ("Folders", PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)),
                
  /** Project category */
  PROJECT ("Projects", PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),
                
  /** Task category */
  TASK    ("Tasks", TasksUiImages.TASK),
  
  /** Date category */
  DATE    ("Dates", SharedImages.CALENDAR);

  private final String text;
  private final ImageDescriptor image;

  private MylynCategory(String text, ImageDescriptor image) {
    this.text = text;
    this.image = image;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return image;
  }

  @Override
  public String getText() {
    return text;
  }
}