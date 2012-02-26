/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking.internal.workbench;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

/**
 * Helper class for loading extensions from
 * {@code rabbit.tracking.workbench.listeners}.
 */
final class ListenerExtension<T> {

  /**
   * Creates a new instance.
   * 
   * @param tag the tag name to match elements when loading
   * @param clazz the expected type or super type of the loaded object
   * @return a new instance, not null
   * @throws NullPointerException if any argument is null
   */
  public static <T> ListenerExtension<T> extension(String tag, Class<T> clazz) {
    return new ListenerExtension<T>(tag, clazz);
  }

  private final String tag;
  private final Class<T> clazz;

  private ListenerExtension(String tag, Class<T> clazz) {
    this.tag = checkNotNull(tag, "tag");
    this.clazz = checkNotNull(clazz, "class");
  }

  /**
   * Loads all the objects from the configuration elements that matches the tag
   * and class configured in this extension.
   * 
   * @param elements the elements to find and create objects from
   * @param errorsBucket to put errors into, if any
   * @return the loaded objects, not null, may be empty, unmodifiable
   * @throws NullPointerException if any argument is null
   */
  public Set<T> load(IConfigurationElement[] elements,
      List<Exception> errorsBucket) {
    checkNotNull(elements, "elements");
    checkNotNull(errorsBucket, "errorsBucket");

    ImmutableSet.Builder<T> builder = ImmutableSet.builder();
    for (IConfigurationElement element : elements) {
      try {
        if (getListenerTag().equalsIgnoreCase(element.getName())) {
          load(element, errorsBucket, builder);
        }
      } catch (Exception e) {
        errorsBucket.add(e);
      }
    }
    return builder.build();
  }

  @SuppressWarnings("unchecked")//
  private void load(IConfigurationElement element, List<Exception> errors,
      ImmutableCollection.Builder<T> builder) throws Exception {

    Object obj = element.createExecutableExtension("class");
    if (getListenerClass().isAssignableFrom(obj.getClass())) {
      builder.add((T)obj);
    } else {
      throw new IllegalArgumentException("Not a "
          + getListenerClass().getSimpleName() + ": " + obj);
    }
  }

  private Class<T> getListenerClass() {
    return clazz;
  }

  private String getListenerTag() {
    return tag;
  }
}
