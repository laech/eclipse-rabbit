package rabbit.tracking.event;

import java.util.Calendar;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class WorkbenchEvent extends ContinuousEvent {

	private IWorkbenchPart workbenchPart;
	private IPerspectiveDescriptor perspective;
	
	public WorkbenchEvent(Calendar time, long duration, IWorkbenchWindow win) {
		super(time, duration);
		setDefaults(win);
	}

	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	public void setWorkbenchPart(IWorkbenchPart workbenchPart) {
		this.workbenchPart = workbenchPart;
	}

	public IPerspectiveDescriptor getPerspective() {
		return perspective;
	}

	public void setPerspective(IPerspectiveDescriptor perspective) {
		this.perspective = perspective;
	}

	protected void setDefaults(final IWorkbenchWindow win) {
		
		if (win == null) {
			return;
		}
		
		win.getWorkbench().getDisplay().syncExec(new Runnable() {
			
			@Override
			public void run() {

				IWorkbenchPage page = win.getActivePage();
				if (page == null) {
					return;
				}

				setPerspective(page.getPerspective());

				IWorkbenchPart part = page.getActivePart();
				setWorkbenchPart(part);
			}
		});
	}
}