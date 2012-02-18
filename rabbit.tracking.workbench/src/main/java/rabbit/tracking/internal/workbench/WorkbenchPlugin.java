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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptySet;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static rabbit.tracking.internal.workbench.ListenerExtension.extension;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.workbench.ICommandEventListener;
import rabbit.tracking.workbench.IFileEventListener;
import rabbit.tracking.workbench.ILaunchEventListener;
import rabbit.tracking.workbench.IPartEventListener;
import rabbit.tracking.workbench.IPerspectiveEventListener;
import rabbit.tracking.workbench.ISessionEventListener;

public final class WorkbenchPlugin extends AbstractUIPlugin {

  private static volatile WorkbenchPlugin plugin;

  public static WorkbenchPlugin getDefault() {
    return plugin;
  }

  private volatile Set<ICommandEventListener> commandListeners;
  private volatile Set<IFileEventListener> fileListeners;
  private volatile Set<ILaunchEventListener> launchListeners;
  private volatile Set<IPartEventListener> partListeners;
  private volatile Set<IPerspectiveEventListener> perspectiveListeners;
  private volatile Set<ISessionEventListener> sessionListeners;

  public WorkbenchPlugin() {
    resetListeners();
  }

  @Override public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
    loadListeners(getExtensionRegistry());
  }

  @Override public void stop(BundleContext context) throws Exception {
    try {
      plugin = null;
      resetListeners();
    } finally {
      super.stop(context);
    }
  }

  void resetListeners() {
    commandListeners = emptySet();
    fileListeners = emptySet();
    launchListeners = emptySet();
    partListeners = emptySet();
    perspectiveListeners = emptySet();
    sessionListeners = emptySet();
  }

  void loadListeners(IExtensionRegistry registry) {
    String pluginId = getBundle().getSymbolicName();

    IConfigurationElement[] elements = registry
        .getConfigurationElementsFor(pluginId, "listeners");

    List<Exception> errorsBucket = newArrayList();

    commandListeners =
        extension("commandEventListener", ICommandEventListener.class)
            .load(elements, errorsBucket);

    fileListeners =
        extension("fileEventListener", IFileEventListener.class)
            .load(elements, errorsBucket);

    launchListeners =
        extension("launchEventListener", ILaunchEventListener.class)
            .load(elements, errorsBucket);

    partListeners =
        extension("partEventListener", IPartEventListener.class)
            .load(elements, errorsBucket);

    perspectiveListeners =
        extension("perspectiveEventListener", IPerspectiveEventListener.class)
            .load(elements, errorsBucket);

    sessionListeners =
        extension("sessionEventListener", ISessionEventListener.class)
            .load(elements, errorsBucket);

    for (Exception e : errorsBucket) {
      getLog().log(new Status(ERROR, pluginId, e.getMessage(), e));
    }
  }

  public Set<ICommandEventListener> getCommandEventListeners() {
    return commandListeners;
  }

  public Set<IFileEventListener> getFileEventListeners() {
    return fileListeners;
  }

  public Set<ILaunchEventListener> getLaunchEventListeners() {
    return launchListeners;
  }

  public Set<IPartEventListener> getPartEventListeners() {
    return partListeners;
  }

  public Set<IPerspectiveEventListener> getPerspectiveEventListeners() {
    return perspectiveListeners;
  }

  public Set<ISessionEventListener> getSessionEventListeners() {
    return sessionListeners;
  }
}
