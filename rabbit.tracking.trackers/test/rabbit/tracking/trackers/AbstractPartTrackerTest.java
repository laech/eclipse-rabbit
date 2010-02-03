package rabbit.tracking.trackers;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractPartTrackerTest<E> extends TrackerTest<E> {

	private IWorkbenchWindow win;
	protected AbstractPartTracker<E> tracker;

	@Before
	public void setup() {
		win = getWorkbenchWindow();
		tracker = createTracker();
	}

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
	
	protected IFile getFileForTesting() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("Tmp");
		
		if (!project.isAccessible()) {
			try {
				project.create(null);
				project.open(null);
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return project.getFile("hello.txt");
	}

	@Test
	public void testAccuracy() throws InterruptedException, PartInitException {

		// Usage an editor instead of a view so that the FileTrackerTest also works.
		
		IFile file = getFileForTesting();
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
		try {
			page.openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		IWorkbenchPart newPart = page.getActiveEditor();

		// Test enable then disable:

		Calendar start = Calendar.getInstance();
		tracker.setEnabled(true);
		TimeUnit.MILLISECONDS.sleep(35);
		tracker.setEnabled(false);
		Calendar end = Calendar.getInstance();
		E event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 35, 1, start, end);

		// Test partActivated then partDeactivated:
		// these two methods are always called when changing views.

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(25);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 25, 1, start, end);

		// Test partActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(70);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 70, 1, start, end);

		// Test windowOpened then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(60);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 60, 1, start, end);

		// Test windowOpened then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(10);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 10, 1, start, end);
		
		// Test windowOpened then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(20);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 20, 1, start, end);
		
		// Test windowActivated then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(30);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 30, 1, start, end);

		// Test windowActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(40);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 40, 1, start, end);
		
		// Test windowActivated then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(50);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		assertAccuracy(event, newPart, 50, 1, start, end);
	}

//	private void assertAccuracy(PartEvent event,
//			IPerspectiveDescriptor pers, IWorkbenchPart part,
//			long durationInMillis, int size, Calendar start, Calendar end) {
//
//		assertEquals(size, tracker.getData().size());
//		assertEquals(part, event.getWorkbenchPart());
//		assertTrue(start.compareTo(event.getTime()) <= 0);
//		assertTrue(end.compareTo(event.getTime()) >= 0);
//		assertTrue(durationInMillis - 10 <= event.getDuration());
//		assertTrue(durationInMillis + 20 >= event.getDuration());
//	}

	protected abstract void assertAccuracy(E event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end);

	@Override
	protected abstract AbstractPartTracker<E> createTracker();

	@Override
	protected abstract E createEvent();
}
