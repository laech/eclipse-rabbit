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

package rabbit.tracking.internal;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

public final class UserMonitorFactory extends AbstractServiceFactory {

  public UserMonitorFactory() {
  }

  @SuppressWarnings("rawtypes")//
  @Override public Object create(
      Class serviceInterface,
      IServiceLocator parentLocator,
      IServiceLocator locator) {
    return UserMonitor.start(PlatformUI.getWorkbench().getDisplay(), 1, MINUTES);
  }
}
