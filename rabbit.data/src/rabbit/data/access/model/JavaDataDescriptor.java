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
package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for time spent on java elements.
 */
public class JavaDataDescriptor extends DurationDescriptor {

  private final String handleIdentifier;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration of the data.
   * @param handlerIdentifier The handle identifier of the java element.
   * @throws NullPointerException If any of the arguments are null.
   * @see IJavaElement#getHandleIdentifier()
   * @see JavaCore#create(String)
   */
  public JavaDataDescriptor(LocalDate date, Duration duration, String handlerIdentifier) {
    super(date, duration);
    this.handleIdentifier = checkNotNull(handlerIdentifier);
  }

  /**
   * Finds the Java element that has the handle identifier. The returned element
   * does not need to exist within the workspace.
   * 
   * @return The Java element, or null if one cannot be create using the handle
   *         identifier.
   * @see #getHandleIdentifier()
   */
  public final IJavaElement findElement() {
    return JavaCore.create(getHandleIdentifier());
  }

  /**
   * Gets the handle identifier of the java element.
   * 
   * @return The handle identifier.
   * @see IJavaElement#getHandleIdentifier()
   * @see JavaCore#create(String)
   */
  public final String getHandleIdentifier() {
    return handleIdentifier;
  }

}
