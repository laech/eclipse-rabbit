package rabbit.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.events.PartEvent;
import rabbit.core.internal.storage.xml.PartEventStorer;

/**
 * Tracks workbench part usage.
 */
public class PartTracker extends AbstractPartTracker<PartEvent> implements IPartListener, IWindowListener {

	public PartTracker() {
		super();
	}

	@Override
	protected PartEventStorer createDataStorer() {
		return new PartEventStorer();
	}

	@Override
	protected PartEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		return new PartEvent(time, duration, p);
	}

}
