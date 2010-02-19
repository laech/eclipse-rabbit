package rabbit.core.internal;

import java.util.Observable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Utility class for detecting user idleness.
 * 
 * <p>
 * When a user enters an inactive state from an active state, or when the user
 * enters an active state from an inactive state, the observers will be
 * notified, then the observers can call {@link #isUserActive()} to check the
 * current user state.
 * </p>
 * <p>
 * When {@link #isRunning()} is false, no observers will be notified.
 * </p>
 */
public final class IdleDetector extends Observable implements Listener {

	private ScheduledThreadPoolExecutor timer;
	private ScheduledFuture<?> currentTask;
	private Display display;
	private boolean isRunning;
	private boolean isActive;
	private long lastEventTime;
	private long idleInterval;
	private long runDelay;

	private Runnable taskCode = new Runnable() {
		@Override
		public void run() {
			if (!isActive) {
				return;
			}
			long duration = now() - lastEventTime;
			if (duration > idleInterval) {
				isActive = false;
				setChanged();
				notifyObservers();
			}
		}
	};

	private Runnable addFilters = new Runnable() {
		@Override
		public void run() {
			display.addFilter(SWT.KeyDown, IdleDetector.this);
			display.addFilter(SWT.MouseDown, IdleDetector.this);
		}
	};

	private Runnable removeFilters = new Runnable() {
		@Override
		public void run() {
			display.removeFilter(SWT.KeyDown, IdleDetector.this);
			display.removeFilter(SWT.MouseDown, IdleDetector.this);
		}
	};

	/**
	 * Constructor. When constructed, this object is not yet running.
	 * 
	 * @param disp
	 *            The display to listen to.
	 * @param idleTime
	 *            After no activities within this period (in milliseconds), the
	 *            user is considered idle.
	 * @param delay
	 *            The time (in milliseconds) of how often checks should run.
	 * @throws NullPointerException
	 *             If display is null.
	 * @throws IllegalArgumentException
	 *             If the interval or the delay is negative.
	 * @see #setRunning(boolean)
	 */
	public IdleDetector(Display disp, long idleTime, long delay) {
		if (disp == null) {
			throw new NullPointerException();
		}
		if (idleTime < 0 || delay < 0) {
			throw new IllegalArgumentException();
		}
		isRunning = false;
		isActive = false;
		runDelay = delay;
		idleInterval = idleTime;
		display = disp;
	}

	public long getIdleInterval() {
		return idleInterval;
	}

	public long getRunDelay() {
		return runDelay;
	}

	/**
	 * Gets the display of this detector.
	 * 
	 * @return The display, never null.
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * Sets whether this object should be running or not. Subsequence calls to
	 * set the same state will have no effects. If the display is disposed, call
	 * this method has no effects.
	 * 
	 * @param run
	 *            True to run, false to stop.
	 */
	public void setRunning(boolean run) {
		if (isRunning == run || display.isDisposed()) {
			return;
		}

		if (run) {
			isRunning = true;
			isActive = true;
			lastEventTime = now();
			display.syncExec(addFilters);
			timer = new ScheduledThreadPoolExecutor(1);
			currentTask = timer.scheduleWithFixedDelay(taskCode, runDelay, runDelay, TimeUnit.MILLISECONDS);
		} else {
			display.syncExec(removeFilters);
			currentTask.cancel(false);
			isRunning = false;
			isActive = false;
			timer.shutdownNow();
		}
	}

	/**
	 * Checks whether this detector is running.
	 * 
	 * @return True if running, false otherwise.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void handleEvent(Event event) {
		lastEventTime = now();
		if (!isActive) {
			isActive = true;
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * Gets the current time in milliseconds.
	 * 
	 * @return The current time in milliseconds.
	 */
	private long now() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

	/**
	 * Checks whether this user is active.
	 * 
	 * @return True if the user is active, false otherwise. <br />
	 *         <b>Note</b>: Value returned cannot be trusted if
	 *         {@link #isRunning()} is false.
	 * @see #isRunning
	 */
	public boolean isUserActive() {
		if (!isRunning) {
			return true;
		}
		return isActive;
	}
}
