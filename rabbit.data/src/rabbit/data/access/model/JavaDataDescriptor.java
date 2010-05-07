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

import com.google.common.base.Objects;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * Data descriptor for time spent on java elements.
 */
public class JavaDataDescriptor extends ValueDescriptor {

  @Nonnull
  private final String handleIdentifier;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param value The value of the data.
   * @param handlerIdentifier The handle identifier of the java element.
   * @throws NullPointerException If date is null, or handleIdentifier is null.
   * @throws IllegalArgumentException If value < 0.
   * @see IJavaElement#getHandleIdentifier()
   * @see JavaCore#create(String)
   */
  public JavaDataDescriptor(@Nonnull LocalDate date, long value,
      @Nonnull String handlerIdentifier) {
    super(date, value);
    checkNotNull(handlerIdentifier);
    this.handleIdentifier = handlerIdentifier;
  }

  /**
   * Gets the handle identifier of the java element.
   * 
   * @return The handle identifier.
   * @see IJavaElement#getHandleIdentifier()
   * @see JavaCore#create(String)
   */
  @Nonnull
  public String getHandleIdentifier() {
    return handleIdentifier;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getHandleIdentifier());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!obj.getClass().equals(getClass())) return false;
    
    JavaDataDescriptor des = (JavaDataDescriptor) obj;
    return Objects.equal(des.getDate(), getDate())
        && Objects.equal(des.getHandleIdentifier(), getHandleIdentifier())
        && des.getValue() == getValue();
  }
}
