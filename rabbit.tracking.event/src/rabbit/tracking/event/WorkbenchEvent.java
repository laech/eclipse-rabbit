package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.ui.IWorkbenchPart;

public class WorkbenchEvent extends ContinuousEvent {

	private IWorkbenchPart workbenchPart;
	
	public WorkbenchEvent(Calendar time, long duration, IWorkbenchPart part) {
		super(time, duration);
		setWorkbenchPart(part);
	}

	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	public void setWorkbenchPart(IWorkbenchPart workbenchPart) {
		this.workbenchPart = workbenchPart;
	}
}