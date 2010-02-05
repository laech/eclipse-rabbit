package rabbit.tracking.trackers;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener3;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.event.PerspectiveEvent;
import rabbit.tracking.storage.xml.PerspectiveEventStorer;

/**
 * Tracker for tracking on perspective usage.
 */
public class PerspectiveTracker extends AbstractTracker<PerspectiveEvent> implements IPerspectiveListener3, IWindowListener {

	/** Start time of a session, in nanoseconds. */
	private long start;

	/**
	 * Constructor.
	 */
	public PerspectiveTracker() {}

	@Override protected PerspectiveEventStorer createDataStorer() {
		return new PerspectiveEventStorer();
	}

	@Override protected void doDisable() {
		checkState(false);
		for (IWorkbenchWindow win : getWorkbenchWindows())
			win.removePerspectiveListener(this);
	}

	@Override protected void doEnable() {
		checkState(true);
		for (IWorkbenchWindow win : getWorkbenchWindows())
			win.addPerspectiveListener(this);
	}

	/**
	 * Checks the current workbench state, and perform the appropriate actions.
	 * 
	 * @param isEnabling true to indicate this tracker is enabling, false to
	 *            indicate this tracker is disabling.
	 */
	private void checkState(final boolean isEnabling) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			@Override public void run() {
				IWorkbenchWindow win = workbench.getActiveWorkbenchWindow();
				if (win == null)
					return;

				IWorkbenchPage page = win.getActivePage();
				if (page == null)
					return;

				if (page.getPerspective() != null) {
					if (isEnabling)
						startSession();
					else
						endSession(page.getPerspective());
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
	 */
	private void startSession() {
		start = System.nanoTime();
	}

	/**
	 * Ends a session.
	 * 
	 * @param p The perspective to generate an event object from.
	 */
	private void endSession(IPerspectiveDescriptor p) {
		long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		if (duration <= 0) {
			System.err.println("Perspective tracker duration = " + duration);
			return;
		}
		addData(new PerspectiveEvent(Calendar.getInstance(), duration, p));
	}

	/**
	 * Starts or stops a session if the conditions are OK.
	 * 
	 * @param win The session subject.
	 * @param start true to start a session, false to stop a session.
	 */
	private void tryStartOrEndSession(IWorkbenchWindow win, boolean start) {
		IWorkbenchPage page = win.getActivePage();
		if (page == null) {
			return;
		}
		if (page.getPerspective() != null) {
			if (start)
				startSession();
			else
				endSession(page.getPerspective());
		}
	}

	@Override public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		startSession();
		/*
		 * Note: perspectiveActivated is also called when a new perspective is
		 * opened and become active.
		 */
	}

	@Override public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		endSession(perspective);
		/*
		 * Note: perspectiveDeactivated is also called when an active
		 * perspective is closed.
		 */
	}

	@Override public void windowActivated(IWorkbenchWindow window) {
		// Starts tracking if there is an active perspective in this window.
		tryStartOrEndSession(window, true);
	}

	@Override public void windowDeactivated(IWorkbenchWindow window) {
		// Stops tracking if there is an active perspective in this window.
		tryStartOrEndSession(window, false);
	}

	@Override public void windowOpened(IWorkbenchWindow window) {
		window.addPerspectiveListener(this);
		// Starts tracking if there is an active perspective in this window.
		tryStartOrEndSession(window, true);
	}

	@Override public void windowClosed(IWorkbenchWindow window) {
		window.removePerspectiveListener(this);
		// Stops tracking if there is an active perspective in this window.
		tryStartOrEndSession(window, false);
	}

	@Override public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

	@Override public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {}

	@Override public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {}

	@Override public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective) {}

	@Override public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {}

}
