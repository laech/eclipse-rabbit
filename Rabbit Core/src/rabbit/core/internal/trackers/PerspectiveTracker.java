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
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rabbit.core.RabbitCore;
import rabbit.core.events.PerspectiveEvent;
import rabbit.core.storage.IStorer;

/**
 * Tracker for tracking on perspective usage.
 */
public class PerspectiveTracker extends AbstractTracker<PerspectiveEvent>
		implements IPerspectiveListener3, IWindowListener, Observer {

	/** Start time of a session, in nanoseconds. */
	private long start;
	private IPerspectiveDescriptor currentPerspective;

	/**
	 * Constructor.
	 */
	public PerspectiveTracker() {
		super();
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		startSession(perspective);
		/*
		 * Note: perspectiveActivated is also called when a new perspective is
		 * opened and become active.
		 */
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
			String changeId) {
	}

	@Override
	public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
	}

	@Override
	public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		tryEndSession();
		/*
		 * Note: perspectiveDeactivated is also called when an active
		 * perspective is closed.
		 */
	}

	@Override
	public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
	}

	@Override
	public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective,
			IPerspectiveDescriptor newPerspective) {
	}

	@Override
	public void update(java.util.Observable o, Object arg) {
		if (o == RabbitCore.getDefault().getIdleDetector() && isEnabled()) {
			checkState(RabbitCore.getDefault().getIdleDetector().isUserActive());
		}
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		// Starts tracking if there is an active perspective in this window.
		tryStartSession(window);
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		window.removePerspectiveListener(this);
		// Stops tracking if there is an active perspective in this window.
		tryEndSession();
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		// Stops tracking if there is an active perspective in this window.
		tryEndSession();
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		window.addPerspectiveListener(this);
		// Starts tracking if there is an active perspective in this window.
		tryStartSession(window);
	}

	@Override
	protected IStorer<PerspectiveEvent> createDataStorer() {
		return RabbitCore.getStorer(PerspectiveEvent.class);
	}

	@Override
	protected void doDisable() {
		checkState(false);
		for (IWorkbenchWindow win : getWorkbenchWindows()) {
			win.removePerspectiveListener(this);
		}
		RabbitCore.getDefault().getIdleDetector().deleteObserver(this);
		PlatformUI.getWorkbench().removeWindowListener(this);
	}

	@Override
	protected void doEnable() {
		checkState(true);
		for (IWorkbenchWindow win : getWorkbenchWindows()) {
			win.addPerspectiveListener(this);
		}
		RabbitCore.getDefault().getIdleDetector().addObserver(this);
		PlatformUI.getWorkbench().addWindowListener(this);
	}

	/**
	 * Checks the current workbench state, and perform the appropriate actions.
	 * 
	 * @param startSession
	 *            true to indicate a new session should be started, false to
	 *            indicate a session should be ended.
	 */
	private void checkState(final boolean startSession) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (startSession == false) {
					tryEndSession();
					return;
				}
				IWorkbenchWindow win = workbench.getActiveWorkbenchWindow();
				if (win == null) {
					return;
				}
				IWorkbenchPage page = win.getActivePage();
				if (page == null) {
					return;
				}
				if (page.getPerspective() != null) {
					startSession(page.getPerspective());
				}
			}
		});
	}

	/**
	 * Gets all currently opened workbench windows.
	 * 
	 * @return The currently opened workbench windows.
	 */
	private IWorkbenchWindow[] getWorkbenchWindows() {
		return PlatformUI.getWorkbench().getWorkbenchWindows();
	}

	/**
	 * Starts a session.
	 * 
	 * @param p
	 *            The perspective.
	 * @throws NullPointerException
	 *             If the parameter is null.
	 */
	private void startSession(IPerspectiveDescriptor p) {
		if (p == null) {
			throw new NullPointerException();
		}
		start = System.nanoTime();
		currentPerspective = p;
	}

	/**
	 * Ends a session.
	 * 
	 * @param p
	 *            The perspective to generate an event object from.
	 */
	private void tryEndSession() {
		if (start == Long.MAX_VALUE || currentPerspective == null) {
			return;
		}
		long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		if (duration > 0) {
			addData(new PerspectiveEvent(Calendar.getInstance(), duration, currentPerspective));
		}
		start = Long.MAX_VALUE;
		currentPerspective = null;
	}

	private void tryStartSession(IWorkbenchWindow win) {
		IWorkbenchPage page = win.getActivePage();
		if (page == null) {
			return;
		}
		if (page.getPerspective() != null) {
			startSession(page.getPerspective());
		}
	}

}
