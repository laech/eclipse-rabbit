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

package rabbit.workbench.internal.tracking;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptySet;
import static org.eclipse.debug.core.DebugEvent.CREATE;
import static org.eclipse.debug.core.DebugEvent.SUSPEND;
import static org.eclipse.debug.core.DebugEvent.TERMINATE;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IThread;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.util.IClock;
import rabbit.workbench.tracking.event.LaunchEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;

public final class LaunchTracker extends AbstractTracker {

  private final IDebugEventSetListener listener = new IDebugEventSetListener() {
    @Override public void handleDebugEvents(DebugEvent[] events) {
      for (DebugEvent event : events) {
        try {
          handleDebugEvent(event);
        } catch (CoreException letThisEventGo) {
        }
      }
    }
  };

  private final EventBus bus;
  private final IClock clock;
  private final DebugPlugin plugin;

  private final Map<ILaunch, Instant> launchStarts;
  private final SetMultimap<ILaunch, IPath> launchFiles;

  public LaunchTracker(EventBus bus, IClock clock, DebugPlugin plugin) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.plugin = checkNotNull(plugin, "plugin");
    this.launchFiles = HashMultimap.create();
    this.launchStarts = newHashMap();
  }

  @Override protected void onStart() {
    plugin.addDebugEventListener(listener);
  }

  @Override protected void onStop() {
    plugin.removeDebugEventListener(listener);
  }

  private void handleDebugEvent(DebugEvent event) throws CoreException {
    Object source = event.getSource();

    if (source instanceof IProcess) {
      handleProcess(event, (IProcess)source);

    } else if (source instanceof IThread) {
      handleThread(event, (IThread)source);
    }
  }

  private void handleProcess(DebugEvent event, IProcess process)
      throws CoreException {
    switch (event.getKind()) {
    case CREATE:
      handleProcessCreation(process.getLaunch());
      break;
    case TERMINATE:
      handleProcessTermination(process.getLaunch());
      break;
    }
  }

  private void handleProcessCreation(ILaunch launch) {
    Instant instant = clock.now();
    synchronized (LaunchTracker.this) {
      launchStarts.put(launch, instant);
    }
  }

  private void handleProcessTermination(ILaunch launch) throws CoreException {
    Instant start;
    Set<IPath> files;

    synchronized (LaunchTracker.this) {
      start = launchStarts.get(launch);
      files = launchFiles.get(launch);
    }

    if (start == null)
      return;

    if (files == null)
      files = emptySet();

    ILaunchConfiguration config = launch.getLaunchConfiguration();
    if (config == null)
      return;

    ILaunchConfigurationType type = config.getType();
    if (type == null)
      return;

    Duration duration = new Duration(start, clock.now());
    bus.post(new LaunchEvent(start, duration, launch, config, type, files));
  }

  private void handleThread(DebugEvent event, IThread thread)
      throws DebugException {
    if (event.getKind() != SUSPEND)
      return;

    ILaunch launch = thread.getLaunch();
    ISourceLocator locator = launch.getSourceLocator();
    if (locator == null)
      return;

    Object elem = locator.getSourceElement(thread.getTopStackFrame());
    if (elem instanceof IFile) {
      synchronized (LaunchTracker.this) {
        launchFiles.put(launch, ((IFile)elem).getFullPath());
      }
    }
  }

}
