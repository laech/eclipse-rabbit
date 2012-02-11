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

import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eclipse.core.runtime.SafeRunner.run;

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.workbench.ICommandEventListener;
import rabbit.tracking.workbench.IFileEventListener;
import rabbit.tracking.workbench.ILaunchEventListener;
import rabbit.tracking.workbench.IPartEventListener;
import rabbit.tracking.workbench.IPerspectiveEventListener;
import rabbit.tracking.workbench.ISessionEventListener;

public final class WorkbenchPlugin extends AbstractUIPlugin {

  private static WorkbenchPlugin plugin;

  public static WorkbenchPlugin getDefault() {
    return plugin;
  }

  public WorkbenchPlugin() {
  }

  @Override public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override public void stop(BundleContext context) throws Exception {
    try {
      plugin = null;
    } finally {
      super.stop(context);
    }
  }

  private <T> Set<T> loadListeners(Class<T> listenerClass) {
    IConfigurationElement[] elements = getExtensionRegistry()
        .getConfigurationElementsFor("rabbit.tracking.workbench", "listeners");

    for (final IConfigurationElement element : elements) {
      run(new SafeRunnable() {
        @Override public void run() throws Exception {
          Object obj = element.createExecutableExtension("class");
          if (obj instanceof ICommandEventListener) {
            
          } else if (obj instanceof IFileEventListener) {
            
          } else if (obj instanceof ILaunchEventListener) {
            
          } else if (obj instanceof IPartEventListener) {
            
          } else if (obj instanceof IPerspectiveEventListener) {
            
          } else if (obj instanceof ISessionEventListener) {
            
          } else {
            throw new IllegalArgumentException(); // TODO
          }
        }
      });
    }
  }
}
