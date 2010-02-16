package rabbit.core.internal;

import java.util.Observable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class IdleDetector extends Observable implements Listener {

	private Display display;
	private ScheduledThreadPoolExecutor timer;
	private ScheduledFuture<?> currentTask;
	private Runnable taskCode;
	private boolean isRunning;

	private boolean isActive;
	private long lastEventTime;

	private Runnable addFilters = new Runnable() {
		public void run() {
			display.addFilter(SWT.KeyDown, IdleDetector.this);
			display.addFilter(SWT.MouseDown, IdleDetector.this);
		}
	};

	private Runnable removeFilters = new Runnable() {
		public void run() {
			display.removeFilter(SWT.KeyDown, IdleDetector.this);
			display.removeFilter(SWT.MouseDown, IdleDetector.this);
		}
	};

	public IdleDetector(Display disp, final long idleInterval) {
		display = disp;
		timer = new ScheduledThreadPoolExecutor(1);
		taskCode = new Runnable() {
			@Override
			public void run() {
				if (!isActive) {
					return;
				}

				long duration = now() - lastEventTime;
				if (duration > idleInterval) {
					isActive = false;
					System.out.println("not active: " + duration);

					setChanged();
					notifyObservers();
				}
			}
		};
	}

	public void setRunning(boolean run) {
		if (isRunning == run) {
			return;
		}

		if (run) {
			display.syncExec(addFilters);
			currentTask = timer.scheduleWithFixedDelay(taskCode, 10, 1, TimeUnit.SECONDS);
			isRunning = true;
			isActive = true;
			lastEventTime = now();
		} else {
			display.syncExec(removeFilters);
			currentTask.cancel(false);
			isRunning = false;
			isActive = false;
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public void handleEvent(Event event) {
		if (!isActive) {
			isActive = true;
			lastEventTime = now();
			System.out.println("active");

			setChanged();
			notifyObservers();
		}
	}

	public long now() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

	public boolean isUserActive() {
		return isActive;
	}
}
