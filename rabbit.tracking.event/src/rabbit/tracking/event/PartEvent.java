package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.ui.IWorkbenchPart;

/**
 * Represents a workbench part event.
 */
public class PartEvent extends ContinuousEvent {

	private IWorkbenchPart workbenchPart;

	/**
	 * Constructs a new event.
	 * 
	 * @param time The end time of the event.
	 * @param duration The duration of the event, in milliseconds.
	 * @param part The workbench part.
	 */
	public PartEvent(Calendar time, long duration, IWorkbenchPart part) {
		super(time, duration);
		setWorkbenchPart(part);
	}

	/**
	 * Gets the workbench part.
	 * 
	 * @return The workbench part.
	 */
	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	/**
	 * Sets the workbench part. TODO: check for null?
	 * 
	 * @param workbenchPart The workbench part.
	 */
	public void setWorkbenchPart(IWorkbenchPart workbenchPart) {
		this.workbenchPart = workbenchPart;
	}
}
