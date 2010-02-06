package rabbit.core.internal.trackers;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Defines common behaviors for part trackers.
 * 
 * @param <E>
 *            The event type that is being tracked.
 */
public abstract class AbstractPartTracker<E> extends AbstractTracker<E> implements IPartListener, IWindowListener {

	private long start;

	/**
	 * Constructor.
	 */
	public AbstractPartTracker() {
		super();
	}

	@Override
	protected void doDisable() {
		PlatformUI.getWorkbench().removeWindowListener(this);
		for (IPartService s : getPartServices())
			s.removePartListener(this);

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				if (win != null && win.getPartService().getActivePart() != null)
					endSession(win.getPartService().getActivePart());
			}
		});
	}

	@Override
	protected void doEnable() {
		PlatformUI.getWorkbench().addWindowListener(this);
		for (IPartService s : getPartServices())
			s.addPartListener(this);

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				if (win != null && win.getPartService().getActivePart() != null)
					startSession();
			}
		});
	}

	/**
	 * Gets all the {@link IPartService} from the currently opened windows.
	 * 
	 * @return A Set of IPartService.
	 */
	private Set<IPartService> getPartServices() {
		Set<IPartService> result = new HashSet<IPartService>();
		IWorkbenchWindow[] ws = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow w : ws) {
			result.add(w.getPartService());
		}
		return result;
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		startSession();
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		endSession(part);
	}

	/**
	 * Starts a new session.
	 */
	protected void startSession() {
		start = System.nanoTime();
	}

	/**
	 * Ends a session.
	 * 
	 * @param part
	 *            The part to get data from.
	 */
	protected void endSession(IWorkbenchPart part) {
		long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		if (duration <= 0)
			return;

		start = Long.MAX_VALUE;
		E event = tryCreateEvent(Calendar.getInstance(), duration, part);
		if (event != null)
			addData(event);
	}

	/**
	 * Try to create an event. This method is called when a session ends.
	 * 
	 * @param time
	 *            The time of the event.
	 * @param duration
	 *            The duration of the event.
	 * @param p
	 *            The workbench part of the event.
	 * @return An event, or null if one should not be created.
	 */
	protected abstract E tryCreateEvent(Calendar time, long duration, IWorkbenchPart p);

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		if (window.getPartService().getActivePart() != null)
			startSession();
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		window.getPartService().removePartListener(this);
		if (window.getPartService().getActivePart() != null)
			endSession(window.getPartService().getActivePart());
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		if (window.getPartService().getActivePart() != null)
			endSession(window.getPartService().getActivePart());
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		window.getPartService().addPartListener(this);
		if (window.getPartService().getActivePart() != null)
			startSession();
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart p) {
	}
}
