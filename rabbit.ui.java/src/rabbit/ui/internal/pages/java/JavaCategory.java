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
package rabbit.ui.internal.pages.java;

import rabbit.ui.internal.util.ICategory;

import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC;
import static org.eclipse.jdt.ui.JavaUI.getSharedImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import javax.annotation.Nonnull;

/**
 * Categories supported by {@link JavaPageContentProvider}.
 */
public enum JavaCategory implements ICategory {
  
  /** Date category */
  DATE        ("Dates", rabbit.ui.internal.SharedImages.CALENDAR),
  
  /** Java project category */
  PROJECT     ("Projects", PlatformUI.getWorkbench().getSharedImages()
                  .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),
                  
  /** Java package root (source folder) category */
  PACKAGE_ROOT("Source Folders", getSharedImages().getImageDescriptor(IMG_OBJS_PACKFRAG_ROOT)),
  
  /** Java package category */
  PACKAGE     ("Packages", getSharedImages().getImageDescriptor(IMG_OBJS_PACKAGE)),
  
  /** Java type root (Java file) category */
  TYPE_ROOT   ("Files", getSharedImages().getImageDescriptor(IMG_OBJS_CUNIT)),
  
  /** Java type category */
  TYPE        ("Types", getSharedImages().getImageDescriptor(IMG_OBJS_CLASS)),
  
  /** Java method category */
  METHOD      ("Methods", getSharedImages().getImageDescriptor(IMG_OBJS_PUBLIC)),
  ;
  
  @Nonnull private final String text;
  @Nonnull private final ImageDescriptor image;
  
  private JavaCategory(@Nonnull String text, @Nonnull ImageDescriptor image) {
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