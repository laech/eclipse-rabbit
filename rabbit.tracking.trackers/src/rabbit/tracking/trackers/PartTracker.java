package rabbit.tracking.trackers;

import java.util.Calendar;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;

import rabbit.tracking.event.PartEvent;
import rabbit.tracking.storage.xml.PartEventStorer;

public class PartTracker extends AbstractPartTracker<PartEvent> implements
		IPartListener, IWindowListener {

	public PartTracker() {
		super();
	}

	@Override
	protected PartEventStorer createDataStorer() {
		return new PartEventStorer();
	}

	@Override
	protected PartEvent createEvent(Calendar time, long duration,
			IWorkbenchPart p) {
		return new PartEvent(time, duration, p);
	}

}
