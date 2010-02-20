package rabbit.core.internal.trackers;

import java.util.Calendar;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.core.RabbitCore;
import rabbit.core.events.PartEvent;
import rabbit.core.storage.IStorer;

/**
 * Tracks workbench part usage.
 */
public class PartTracker extends AbstractPartTracker<PartEvent> implements IPartListener, IWindowListener {

	public PartTracker() {
		super();
	}

	@Override
	protected IStorer<PartEvent> createDataStorer() {
		return RabbitCore.getStorer(PartEvent.class);
	}

	@Override
	protected PartEvent tryCreateEvent(Calendar time, long duration, IWorkbenchPart p) {
		return new PartEvent(time, duration, p);
	}

}
