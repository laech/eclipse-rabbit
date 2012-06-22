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
package rabbit.tracking.internal;

import static java.util.Collections.emptySet;
import static org.eclipse.core.runtime.Platform.getExtensionRegistry;
import static org.eclipse.core.runtime.SafeRunner.run;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.ITracker;

import com.google.common.collect.ImmutableSet;

public final class TrackingPlugin extends AbstractUIPlugin
    implements IWorkbenchListener {

  private static TrackingPlugin plugin;

  public static TrackingPlugin getDefault() {
    return plugin;
  }

  private volatile Set<ITracker> trackers;

  public TrackingPlugin() {
    trackers = emptySet();
  }

  @Override public void postShutdown(IWorkbench workbench) {
    // Everything should be done before the workbench is shut down, use
    // preShutdown method instead
  }

  @Override public boolean preShutdown(IWorkbench workbench, boolean forced) {
    setEnable(trackers, false);
    // saveCurrentData();
    return true;
  }

  // TODO implement save with pub/sub style

  // /**
  // * Call this method to saves all current data collected by the trackers now.
  // */
  // public void saveCurrentData() {
  // for (ITracker tracker : trackers) {
  // if (tracker instanceof IPersistable) {
  // ((IPersistable)tracker).save();
  // }
  // }
  // }

  @Override public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
    trackers = loadTrackers();
    setEnable(trackers, true);
    getWorkbench().addWorkbenchListener(this);
  }

  @Override public void stop(BundleContext context) throws Exception {
    try {
      getWorkbench().removeWorkbenchListener(this);
      setEnable(trackers, false);
      // saveCurrentData();
      trackers = emptySet();
      plugin = null;
    } finally {
      super.stop(context);
    }
  }

  private Set<ITracker> loadTrackers() {
    IConfigurationElement[] elements = getExtensionRegistry()
        .getConfigurationElementsFor("rabbit.tracking", "trackers");

    final ImmutableSet.Builder<ITracker> builder = ImmutableSet.builder();
    for (final IConfigurationElement element : elements) {
      run(new SafeRunnable() {
        @Override public void run() throws Exception {
          Object o = element.createExecutableExtension("class");
          if (o instanceof ITracker) {
            builder.add((ITracker)o);
          } else {
            throw new IllegalArgumentException("Not a tracker: " + o);
          }
        }
      });
    }
    return builder.build();
  }

  private void setEnable(Collection<ITracker> trackers, boolean enable) {
    for (ITracker tracker : trackers) {
      if (enable) {
        tracker.start();
      } else {
        tracker.stop();
      }
    }
  }
}
