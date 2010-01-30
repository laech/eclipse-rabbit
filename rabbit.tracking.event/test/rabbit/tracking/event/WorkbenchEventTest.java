package rabbit.tracking.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Random;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

public class WorkbenchEventTest extends ContinuousEventTest {
	
	private IWorkbenchWindow win = getWorkbenchWindow(); 
	private IPerspectiveDescriptor perspective = getWorkbenchWindow().getActivePage().getPerspective();
	private IWorkbenchPart part = getWorkbenchWindow().getPartService().getActivePart();
	
	private WorkbenchEvent event = createEvent(Calendar.getInstance(), 10);
	
	public IWorkbenchWindow getWorkbenchWindow() {
		
		final IWorkbench wb = PlatformUI.getWorkbench();
		wb.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				win = wb.getActiveWorkbenchWindow();
			}
		});
		return win;
	}
	
	@Override
	protected WorkbenchEvent createEvent(Calendar time, long duration) {
		return new WorkbenchEvent(time, duration, getWorkbenchWindow());
	}
	
	@Test
	public void testWorkbenchEvent() {
		assertNotNull(event);
	}

	@Test
	public void testGetWorkbenchPart() {
		assertSame(part, event.getWorkbenchPart());
	}

	@Test
	public void testSetWorkbenchPart() {
		
		try {
			IWorkbenchPart newP = win.getActivePage().showView("org.eclipse.ui.navigator.ProjectExplorer");
			event.setWorkbenchPart(newP);
			assertSame(newP, event.getWorkbenchPart());
			
		} catch (PartInitException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetPerspective() {
		assertSame(perspective, event.getPerspective());
	}

	@Test
	public void testSetPerspective() {
		
		Random random = new Random();
		IPerspectiveDescriptor newP = win.getWorkbench().getPerspectiveRegistry().getPerspectives()[random.nextInt(5)];
		event.setPerspective(newP);
		assertSame(newP, event.getPerspective());
	}

	@Test
	public void testSetDefaults() {
		
		try {
			Random random = new Random();
			
			IPerspectiveDescriptor newPers = win.getWorkbench().getPerspectiveRegistry().getPerspectives()[random.nextInt(5)];
			// Make sure the new perspective is not the old one.
			assertNotSame("Perspective already opened, please choose another one", 
					newPers, event.getPerspective());
			// Then set the new perspective.
			win.getActivePage().setPerspective(newPers); 
			
			String partId = "org.eclipse.ui.views.ProblemView";
			// Make sure the new part is not the old one.
			assertFalse("View already opened, please choose another one.", 
					partId.equals(event.getWorkbenchPart().getSite().getId()));
			// Then open it.
			IWorkbenchPart newPart = win.getActivePage().showView(partId); 
			
			event.setDefaults(win);
			assertSame(newPers, event.getPerspective());
			assertSame(newPart, event.getWorkbenchPart());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
