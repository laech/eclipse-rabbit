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
package rabbit.ui.internal.util;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.debug.core.ILaunchMode;

// TODO equals and hashcode
public class UndefinedLaunchMode implements ILaunchMode {
  
  private final String identifier;
  
  public UndefinedLaunchMode(String id) {
    checkNotNull(id);
    identifier = id;
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getLabel() {
    return getIdentifier();
  }

  @Override
  public String getLaunchAsLabel() {
    return getIdentifier();
  }

}
