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
package rabbit.ui.internal.pages.java;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Shared images of this plug-in.
 */
public class SharedImages {
  
  /** An image descriptor for missing {@link IType} elements. */
  public static final ImageDescriptor TYPE_ALT = getObj16("type_alt.gif");
  
  /** An image descriptor for missing {@link IMethod} elements. */
  public static final ImageDescriptor METHOD_ALT = getObj16("method_alt.gif");
  
  /**
   * Gets the image from the "obj16" image folder.
   * @param imageName The file name of the image.
   * @return The image descriptor, or null if not found.
   */
  private static ImageDescriptor getObj16(String imageName) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(
        "rabbit.ui.java", "icons/full/obj16/" + imageName);
  }
}
