package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.ui.IPerspectiveDescriptor;

public class PerspectiveEvent extends ContinuousEvent {

	private IPerspectiveDescriptor perspective;
	
	public PerspectiveEvent(Calendar time, long duration, IPerspectiveDescriptor p) {
		super(time, duration);
		setPerspective(p);
	}

	public IPerspectiveDescriptor getPerspective() {
		return perspective;
	}

	public void setPerspective(IPerspectiveDescriptor perspective) {
		this.perspective = perspective;
	}

}
