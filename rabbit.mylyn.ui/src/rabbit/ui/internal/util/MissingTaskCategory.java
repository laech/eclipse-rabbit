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
package rabbit.ui.internal.util;

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// TODO re-test
@Deprecated
public final class MissingTaskCategory extends AbstractTaskContainer {

  public MissingTaskCategory() {
    super("rabbit.mylyn.ui.MissingTaskCategory");
  }

  @Override
  public void setHandleIdentifier(String handleIdentifier) {
  }
  
  @Override
  public void setUrl(String url) {
  }
  
  @Override
  public String getSummary() {
    return "Missing Tasks";
  }
}
