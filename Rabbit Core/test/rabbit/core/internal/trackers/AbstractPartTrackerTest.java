/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;
import rabbit.core.events.ContinuousEvent;
import rabbit.core.internal.IdleDetector;

/**
 * Test {@link AbstractPartTracker}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractPartTrackerTest<E extends ContinuousEvent> extends
		AbstractTrackerTest<E> {

	protected AbstractPartTracker<E> tracker;

	protected static SWTWorkbenchBot bot;

	@BeforeClass
	public static void setUpBeforeClass() {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
		// RabbitCore.getDefault().setIdleDetectionEnabled(false);
	}

	/** Do not use in tests. */
	private IWorkbenchWindow win;

	/** Do not use in tests. */
	private IEditorPart editor;

	@Before
	public void setup() {
		win = getActiveWindow();
		tracker = createTracker();
	}

	/*
	 * Old tests base on calling listener methods.
	 */
	@Test
	public void testAccuracy() throws Exception {

		// Usage an editor instead of a view so that the FileTrackerTest also
		// works.
		IWorkbenchPart newPart = openNewEditor();

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
	public void testAccuracy2() throws Exception {
		IWorkbenchPart newPart = openNewEditor();

		// Assume a part was never activated, calling deactivated should do
		// nothing.

		TimeUnit.MILLISECONDS.sleep(30);
		tracker.partDeactivated(newPart);
		assertEquals(0, tracker.getData().size());

		TimeUnit.MILLISECONDS.sleep(30);
		tracker.windowDeactivated(newPart.getSite().getWorkbenchWindow());
		assertEquals(0, tracker.getData().size());
	}

	@Test
	public void testChangeEditor() throws Exception {
		IEditorPart editor = openNewEditor();

		long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		uiSleep(sleepDuration);
		openNewEditor();
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));
	}

	@Test
	public void testCloseEditor() throws Exception {
		IEditorPart editor = openNewEditor();

		long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		assertEquals(0, tracker.getData().size());
		uiSleep(sleepDuration);
		bot.activeEditor().close();
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));
	}

	@Test
	public void testCloseWindow() throws Exception {
		openNewWindow();
		IEditorPart editor = openNewEditor();

		long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		assertEquals(0, tracker.getData().size());
		uiSleep(sleepDuration);
		bot.activeShell().close();
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));
	}

	@Test
	public void testDisabled() throws Exception {
		tracker.setEnabled(false);

		// Test IPerspectiveListener.
		uiSleep(30);
		openNewEditor();

		assertTrue(tracker.getData().isEmpty());

		// Test IWindowListener.
		uiSleep(20);
		openNewWindow();
		assertTrue(tracker.getData().isEmpty());
		bot.activeShell().close();

		// Test IdleDetector
		uiSleep(35);
		callIdleDetectorToNotify();
		assertTrue(tracker.getData().isEmpty());
	}

	@Test
	public void testEnableThenDisable() throws Exception {
		IEditorPart editor = openNewEditor();

		final long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		uiSleep(sleepDuration);
		tracker.setEnabled(false);
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));
	}

	@Test
	public void testIdleDetector() throws Exception {
		IEditorPart editor = openNewEditor();

		long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		uiSleep(sleepDuration);
		callIdleDetectorToNotify();
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));
	}

	@Test
	public void testWindowDeactivated() {
		IEditorPart editor = openNewEditor();

		long sleepDuration = 30;
		long start = System.currentTimeMillis();
		tracker.setEnabled(true);
		assertEquals(0, tracker.getData().size());
		uiSleep(sleepDuration);
		openNewWindow();
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		E event = tracker.getData().iterator().next();
		assertTrue(start <= event.getTime().getTimeInMillis());
		assertTrue(end >= event.getTime().getTimeInMillis());
		assertTrue((end - start) >= event.getDuration());
		assertTrue(sleepDuration <= event.getDuration());
		assertTrue(hasSamePart(event, editor));

		bot.activeShell().close();
	}

	protected void callIdleDetectorToNotify() throws Exception {
		Field isActive = IdleDetector.class.getDeclaredField("isActive");
		isActive.setAccessible(true);

		Method setChanged = Observable.class.getDeclaredMethod("setChanged");
		setChanged.setAccessible(true);

		Method notifyObservers = Observable.class.getDeclaredMethod("notifyObservers");
		notifyObservers.setAccessible(true);

		IdleDetector detector = RabbitCore.getDefault().getIdleDetector();
		detector.setRunning(true);
		isActive.set(detector, false);
		setChanged.invoke(detector);
		notifyObservers.invoke(detector);
		detector.setRunning(false);
	}

	@Override
	protected abstract E createEvent();

	@Override
	protected abstract AbstractPartTracker<E> createTracker();

	protected IWorkbenchWindow getActiveWindow() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			}
		});
		return win;
	}

	protected IFile getFileForTesting() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("Tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		IFile file = project.getFile(System.nanoTime() + ".txt");
		if (!file.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp", "txt"));
			file.create(stream, false, null);
			stream.close();
		}
		return file;
	}

	protected abstract boolean hasSamePart(E event, IWorkbenchPart part);

	protected abstract void internalAssertAccuracy(E event, IWorkbenchPart part,
			long durationInMillis, int size, Calendar start, Calendar end);

	protected IEditorPart openNewEditor() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					editor = getActiveWindow().getActivePage().openEditor(
							new FileEditorInput(getFileForTesting()),
							"org.eclipse.ui.DefaultTextEditor", true);
				} catch (Exception e) {
					fail();
				}
			}
		});
		return editor;
	}

	protected IWorkbenchWindow openNewWindow() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					win = getActiveWindow().getWorkbench().openWorkbenchWindow(null);
				} catch (Exception e) {
					fail();
				}
			}
		});
		return win;
	}

	protected void uiSleep(final long duration) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration + 1);
				} catch (InterruptedException e) {
					fail();
				}
			}
		});
	}
}
