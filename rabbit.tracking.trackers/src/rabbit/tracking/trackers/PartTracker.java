package rabbit.tracking.trackers;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.event.WorkbenchEvent;
import rabbit.tracking.storage.xml.WorkbenchEventStorer;

public class PartTracker extends Tracker implements IPartListener, IWindowListener {
	
	private long start;
	
	private Set<WorkbenchEvent> data;
	
	public PartTracker() {
		start = Long.MAX_VALUE;
		data = new LinkedHashSet<WorkbenchEvent>();
	}

	@Override
	protected void doDisable() {
		for (IPartService s : getPartServices()) {
			s.removePartListener(this);
		}
		
		if (data.isEmpty()) {
			return;
		}
		
		WorkbenchEventStorer s = new WorkbenchEventStorer();
		s.insert(data);
		s.write();
	}

	@Override
	protected void doEnable() {
		for (IPartService s : getPartServices()) {
			s.addPartListener(this);
		}
		startSession();
	}

	/**
	 * Gets all the {@link IPartService} from the currently opened windows.
	 * @return A Set of IPartService.
	 */
	private Set<IPartService> getPartServices() {
		Set<IPartService> result = new HashSet<IPartService>();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow w : windows) {
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
		endSession(part.getSite().getWorkbenchWindow());
	}

	private void startSession() {
		if (start != Long.MAX_VALUE) {
			throw new IllegalStateException("The previous session is not finished.");
		}
		start = System.nanoTime();
	}

	private void endSession(IWorkbenchWindow win) {
		long duration = System.nanoTime() - start;
		if (duration <= 0) {
			throw new IllegalStateException("Duration cannot be 0 or negative.");
		}
		start = Long.MAX_VALUE;
		
		data.add(new WorkbenchEvent(Calendar.getInstance(), duration, win));
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
		if (window.getPartService().getActivePart() != null) {
			startSession();
		}
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
		window.getPartService().removePartListener(this);
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		if (window.getPartService().getActivePart() != null) {
			endSession(window);
		}
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		window.getPartService().addPartListener(this);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
	}

}
