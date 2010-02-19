package rabbit.core.internal.trackers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rabbit.core.RabbitCore;

/**
 * Defines common behaviors for part trackers.
 * 
 * @param <E>
 *            The event type that is being tracked.
 */
public abstract class AbstractPartTracker<E> extends AbstractTracker<E>
		implements IPartListener, IWindowListener, Observer {

	private long start;
	private Map<IWorkbenchPart, Boolean> partStates;
	// private IWorkbenchPart currentActivePart;

	private Runnable idleDetectorCode;

	/**
	 * Constructor.
	 */
	public AbstractPartTracker() {
		super();
		start = Long.MAX_VALUE;
		partStates = new HashMap<IWorkbenchPart, Boolean>();
		// currentActivePart = null;

		idleDetectorCode = new Runnable() {
			@Override
			public void run() {
				if (!isEnabled()) {
					return;
				}
				IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (win == null) {
					return;
				}
				IWorkbenchPart part = win.getPartService().getActivePart();
				if (part == null) {
					return;
				}
				if (RabbitCore.getDefault().getIdleDetector().isUserActive()) {
					startSession(part);
				} else {
					endSession(part);
				}
			}
		};
	}

	@Override
	protected void doDisable() {
		RabbitCore.getDefault().getIdleDetector().deleteObserver(this);
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
		RabbitCore.getDefault().getIdleDetector().addObserver(this);
		PlatformUI.getWorkbench().addWindowListener(this);
		for (IPartService s : getPartServices())
			s.addPartListener(this);

		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				if (win != null && win.getPartService().getActivePart() != null) {
					System.out.println(win.getPartService().getActivePart());
					startSession(win.getPartService().getActivePart());
				}
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == RabbitCore.getDefault().getIdleDetector()) {
			PlatformUI.getWorkbench().getDisplay().syncExec(idleDetectorCode);
		}
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
		startSession(part);
		// System.out.println("act : " + part);
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		endSession(part);
		// System.out.println("deact : " + part);
	}

	/**
	 * Starts a new session.
	 */
	protected void startSession(IWorkbenchPart part) {
		// if (currentActivePart != null) {
		// partStates.put(currentActivePart, Boolean.FALSE);
		// }
		start = System.nanoTime();
		partStates.put(part, Boolean.TRUE);
	}

	/**
	 * Ends a session.
	 * 
	 * @param part
	 *            The part to get data from.
	 */
	protected void endSession(IWorkbenchPart part) {
		Boolean hasBeenStarted = partStates.get(part);
		if (hasBeenStarted == null || !hasBeenStarted) {
			return;
		}
		partStates.put(part, Boolean.FALSE);
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
		// System.out.println("act : " + window);
		if (window.getPartService().getActivePart() != null)
			startSession(window.getPartService().getActivePart());
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		// System.out.println("clo : " + window);
		window.getPartService().removePartListener(this);
		if (window.getPartService().getActivePart() != null)
			endSession(window.getPartService().getActivePart());
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		// System.out.println("deact : " + window);
		if (window.getPartService().getActivePart() != null)
			endSession(window.getPartService().getActivePart());
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		// System.out.println("opn : " + window);
		window.getPartService().addPartListener(this);
		if (window.getPartService().getActivePart() != null)
			startSession(window.getPartService().getActivePart());
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// System.out.println("br : " + part);
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// System.out.println("clo : " + part);
	}

	@Override
	public void partOpened(IWorkbenchPart p) {
		// System.out.println("opn : " + p);
	}
}
