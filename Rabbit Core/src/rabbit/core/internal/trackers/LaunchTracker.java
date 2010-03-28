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
package rabbit.core.internal.trackers;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

import rabbit.core.RabbitCore;
import rabbit.core.events.LaunchEvent;
import rabbit.core.internal.storage.xml.LaunchEventStorer;
import rabbit.core.storage.IFileMapper;

//TODO test
/**
 * Tracks launch events.
 */
public class LaunchTracker extends AbstractTracker<LaunchEvent>
		implements IDebugEventSetListener {

	private IFileMapper fileMapper;

	/**
	 * A map of launches and launch configurations. The configuration should be
	 * added here as soon as the launch is started, because configuration can be
	 * removed while the launch is running, then
	 * {@link ILaunch#getLaunchConfiguration()} will return null.
	 */
	private Map<ILaunch, ILaunchConfiguration> launchConfigs;

	/** A map of launches and their start time. */
	private Map<ILaunch, Calendar> launchTimes;

	/** A map of launches and the files involved (for debug launches). */
	private Map<ILaunch, Set<String>> launchFiles;

	/**
	 * Constructs a new tracker.
	 */
	public LaunchTracker() {
		fileMapper = RabbitCore.getFileMapper();
		launchTimes = new HashMap<ILaunch, Calendar>();
		launchFiles = new HashMap<ILaunch, Set<String>>();
		launchConfigs = new HashMap<ILaunch, ILaunchConfiguration>();
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			handleDebugEvent(event);
		}
	}

	@Override
	protected LaunchEventStorer createDataStorer() {
		return LaunchEventStorer.getInstance();
	}

	@Override
	protected void doDisable() {
		DebugPlugin debug = DebugPlugin.getDefault();
		debug.removeDebugEventListener(this);
	}

	@Override
	protected void doEnable() {
		DebugPlugin debug = DebugPlugin.getDefault();
		debug.addDebugEventListener(this);
	}

	/**
	 * Handles an event.
	 * 
	 * @param event
	 *            The event.
	 */
	private void handleDebugEvent(DebugEvent event) {
		Object source = event.getSource();

		if (source instanceof IProcess) {
			handleProcessEvent(event, (IProcess) source);

		} else if (source instanceof IThread) {
			handleThreadEvent(event, (IThread) source);
		}
	}

	/**
	 * Handles the event who's source is an IProcess.
	 * 
	 * @param event
	 *            The event.
	 * @param process
	 *            The process of the event.
	 */
	private void handleProcessEvent(DebugEvent event, IProcess process) {
		ILaunch launch = process.getLaunch();

		// Records the start time of this launch:
		if (event.getKind() == DebugEvent.CREATE) {
			launchTimes.put(launch, Calendar.getInstance());
			launchConfigs.put(launch, launch.getLaunchConfiguration());
		}

		// Calculate duration of this launch:
		else if (event.getKind() == DebugEvent.TERMINATE) {

			Calendar startTime = launchTimes.get(launch);
			if (startTime == null) {
				System.err.println("Launch start time not recorded.");
				return;
			}

			long duration = System.currentTimeMillis() - startTime.getTimeInMillis();
			if (duration <= 0) {
				System.err.println("Launch duration is <= 0.");
				return;
			}

			Set<String> fileIds = launchFiles.get(launch);
			if (fileIds == null) {
				fileIds = Collections.emptySet();
			}

			ILaunchConfiguration config = launchConfigs.get(launch);
			if (config == null) {
				System.err.println("Launch configuration is null.");
				return;
			}
			addData(new LaunchEvent(startTime, duration, launch, config, fileIds));
		}
	}

	/**
	 * Handles an event who's source is an IThread.
	 * 
	 * @param event
	 *            The event.
	 * @param thread
	 *            The thread of the event.
	 */
	private void handleThreadEvent(DebugEvent event, IThread thread) {

		// We are only interested in SUSPEND events:
		if (event.getKind() != DebugEvent.SUSPEND) {
			return;
		}

		ILaunch launch = thread.getLaunch();
		ILaunchConfiguration config = launchConfigs.get(launch);
		if (config == null) {
			System.err.println("Launch configuration is null.");
			return;
		}

		IStackFrame stack = null;
		try {
			stack = thread.getTopStackFrame();
		} catch (DebugException e) {
			System.err.println(e.getMessage());
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
			IFile file = (IFile) element;
			Set<String> fileIds = launchFiles.get(launch);
			if (fileIds == null) {
				fileIds = new HashSet<String>();
				launchFiles.put(launch, fileIds);
			}
			fileIds.add(fileMapper.insert(file));
		}
	}
}
