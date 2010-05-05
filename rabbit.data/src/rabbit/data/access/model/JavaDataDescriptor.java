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
package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

// TODO
public class JavaDataDescriptor extends ValueDescriptor {
  
  private final String handleIdentifier;

  public JavaDataDescriptor(@Nonnull LocalDate date, long value,
      @Nonnull String handlerIdentifier) {
    super(date, value);
    checkNotNull(handlerIdentifier);
    this.handleIdentifier = handlerIdentifier;
  }

  public String getHandleIdentifier() {
    return handleIdentifier;
  }
}
