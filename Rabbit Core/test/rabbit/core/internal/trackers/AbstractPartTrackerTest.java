package rabbit.core.internal.trackers;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link AbstractPartTracker}
 */
public abstract class AbstractPartTrackerTest<E> extends AbstractTrackerTest<E> {

	private IWorkbenchWindow win;
	protected AbstractPartTracker<E> tracker;

	@Before
	public void setup() {
		win = getWorkbenchWindow();
		tracker = createTracker();
	}

	/** Gets the currently active window. */
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

	/** Gets a file for testing. */
	protected IFile getFileForTesting() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("Tmp");
		try {
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}
		} catch (Exception e) {
			Assert.fail();
			return null;
		}
		return project.getFile("hello.txt");
	}

	@Test
	public void testAccuracy() throws InterruptedException, PartInitException {

		// Usage an editor instead of a view so that the FileTrackerTest also
		// works.

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
		internalAssertAccuracy(event, newPart, 35, 1, start, end);

		// Test partActivated then partDeactivated:
		// these two methods are always called when changing views.

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(25);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 25, 1, start, end);

		// Test partActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.partActivated(newPart);
		TimeUnit.MILLISECONDS.sleep(70);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 70, 1, start, end);

		// Test windowOpened then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(60);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 60, 1, start, end);

		// Test windowOpened then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(10);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 10, 1, start, end);

		// Test windowOpened then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowOpened(win);
		TimeUnit.MILLISECONDS.sleep(20);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 20, 1, start, end);

		// Test windowActivated then windowDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(30);
		tracker.windowDeactivated(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 30, 1, start, end);

		// Test windowActivated then windowClosed:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(40);
		tracker.windowClosed(win);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 40, 1, start, end);

		// Test windowActivated then partDeactivated:

		tracker.flushData();
		start = Calendar.getInstance();
		tracker.windowActivated(win);
		TimeUnit.MILLISECONDS.sleep(50);
		tracker.partDeactivated(newPart);
		end = Calendar.getInstance();
		event = tracker.getData().iterator().next();
		internalAssertAccuracy(event, newPart, 50, 1, start, end);
	}

	@Test
	public void testAccuracy2() throws InterruptedException {
		IFile file = getFileForTesting();
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		IWorkbenchPage page = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
		try {
			page.openEditor(new FileEditorInput(file), desc.getId());
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		IWorkbenchPart newPart = page.getActiveEditor();

		// Assume a part was never activated, calling deactivated should do
		// nothing.

		TimeUnit.MILLISECONDS.sleep(30);
		tracker.partDeactivated(newPart);
		assertEquals(0, tracker.getData().size());

		TimeUnit.MILLISECONDS.sleep(30);
		tracker.windowDeactivated(page.getWorkbenchWindow());
		assertEquals(0, tracker.getData().size());
	}

	protected abstract void internalAssertAccuracy(E event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end);

	@Override
	protected abstract AbstractPartTracker<E> createTracker();

	@Override
	protected abstract E createEvent();
}
