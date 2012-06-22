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
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static org.eclipse.debug.core.DebugEvent.CREATE;
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
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.IEventListener;
import rabbit.tracking.util.IRecordListener;
import rabbit.tracking.util.IRecorder;
import rabbit.tracking.util.Record;
import rabbit.tracking.util.Recorder;

import com.google.inject.Inject;

public final class LaunchTracker extends AbstractTracker {

  // TODO revisit

  private final IRecordListener<ILaunch> recordListener = new IRecordListener<ILaunch>() {
    @Override public void onRecord(Record<ILaunch> record) {
      ILaunch launch = record.data();
      ILaunchConfiguration config = launch.getLaunchConfiguration();
      if (config == null) {
        return;
      }
      ILaunchConfigurationType type = null;
      try {
        type = config.getType();
      } catch (CoreException e) {
        // TODO
        return;
      }

      Set<IPath> files = launchFiles.get(launch);
      if (files == null) {
        files = emptySet();
      }

      eventListener.onEvent(new LaunchEvent(
          record.instant(), record.duration(), files, launch, config, type));
    }
  };

  private final IDebugEventSetListener listener = new IDebugEventSetListener() {
    @Override public void handleDebugEvents(DebugEvent[] events) {
      for (DebugEvent event : events) {
        handleDebugEvent(event);
      }
    }
  };

  private final DebugPlugin plugin;
  private final IEventListener<ILaunchEvent> eventListener;

  /** A map of launches and the files involved (for debug launches). */
  private final Map<ILaunch, Set<IPath>> launchFiles = newHashMap();

  /** One recorder for each launch. */
  private final Map<ILaunch, IRecorder<ILaunch>> recorders = newHashMap();

  // TODO thread safety

  @Inject public LaunchTracker(
      DebugPlugin plugin,
      IEventListener<ILaunchEvent> listener) {
    this.plugin = checkNotNull(plugin, "plugin");
    this.eventListener = checkNotNull(listener, "listener");
  }

  @Override protected void onStart() {
    plugin.addDebugEventListener(listener);
  }

  @Override protected void onStop() {
    plugin.removeDebugEventListener(listener);
  }

  private void handleDebugEvent(DebugEvent event) {
    Object source = event.getSource();

    if (source instanceof IProcess) {
      handleProcessEvent(event, (IProcess)source);

    } else if (source instanceof IThread) {
      handleThreadEvent(event, (IThread)source);
    }
  }

  private void handleProcessEvent(DebugEvent event, IProcess process) {
    ILaunch launch = process.getLaunch();

    // Records the start time of this launch:
    int kind = event.getKind();
    if (kind == CREATE) {
      IRecorder<ILaunch> recorder = recorders.get(launch);
      if (recorder == null) {
        recorder = Recorder.create();
        recorder.addListener(recordListener);
        recorders.put(launch, recorder);
      }
      recorder.start(launch);

    } else if (kind == TERMINATE) {
      IRecorder<ILaunch> recorder = recorders.get(launch);
      if (recorder != null) {
        recorder.stop();
      }
    }
  }

  private void handleThreadEvent(DebugEvent event, IThread thread) {

    // We are only interested in SUSPEND events:
    if (event.getKind() != DebugEvent.SUSPEND) {
      return;
    }

    ILaunch launch = thread.getLaunch();
    ILaunchConfiguration config = launch.getLaunchConfiguration();
    if (config == null) {
      return;
    }

    IStackFrame stack = null;
    try {
      stack = thread.getTopStackFrame();
    } catch (DebugException e) {
      // TODO
      return;
    }

    if (stack == null) {
      return;
    }

    ISourceLocator sourceLocator = launch.getSourceLocator();
    if (sourceLocator == null) {
      return;
    }

    Object element = sourceLocator.getSourceElement(stack);

    // Element is a file in workspace, record it:
    if (element != null && element instanceof IFile) {
      IFile file = (IFile)element;
      Set<IPath> filePaths = launchFiles.get(launch);
      if (filePaths == null) {
        filePaths = newHashSet();
        launchFiles.put(launch, filePaths);
      }
      filePaths.add(file.getFullPath());
    }
  }

}
