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
package rabbit.mylyn.internal.ui.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

public class MissingTaskCategory implements ITaskContainer, IRepositoryElement {

  private final String summary = "Missing Tasks";
  private final Set<ITask> children = new HashSet<ITask>();
  private static final MissingTaskCategory INSTANCE = new MissingTaskCategory();

  public static MissingTaskCategory getCategory() {
    return INSTANCE;
  }

  private MissingTaskCategory() {
  }

  @Override
  public int compareTo(IRepositoryElement o) {
    return -1;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object getAdapter(Class adapter) {
    return null;
  }

  @Override
  public Collection<ITask> getChildren() {
    return children;
  }

  @Override
  public String getHandleIdentifier() {
    return summary;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public String getUrl() {
    return null;
  }
}
