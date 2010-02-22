package rabbit.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link IdleDetector}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class IdleDetectorTest {

	private static SWTWorkbenchBot bot;
	private static IWorkbenchPage page;

	@BeforeClass
	public static void setUpBeforeClass() {
		bot = new SWTWorkbenchBot();
		bot.viewByTitle("Welcome").close();
		page = bot.activeView().getReference().getPage();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		bot.sleep(2000);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withDisplayNull() {
		new IdleDetector(null, 10, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_withNegativeInterval() {
		new IdleDetector(PlatformUI.getWorkbench().getDisplay(), -1, 10);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_withNegativeDelay() {
		new IdleDetector(PlatformUI.getWorkbench().getDisplay(), 10, -1);
	}

	@Test
	public void testGetDisplay() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Assert.assertNotNull(display);
		Assert.assertSame(display, new IdleDetector(display, 10, 10).getDisplay());
	}

	@Test
	public void testGetRunDelay() {
		long runDelay = 1231;
		Assert.assertEquals(runDelay, new IdleDetector(
				PlatformUI.getWorkbench().getDisplay(), 101010, runDelay).getRunDelay());
	}

	@Test
	public void testGetIdleInterval() {
		long idleInterval = 1936l;
		Assert.assertEquals(idleInterval, new IdleDetector(
				PlatformUI.getWorkbench().getDisplay(), idleInterval, 1).getIdleInterval());
	}

	@Test
	public void testIsRunning() {
		Assert.assertFalse(new IdleDetector(PlatformUI.getWorkbench().getDisplay(), 10, 10).isRunning());
	}

	@Test
	public void testIsUserActive() {
		Assert.assertTrue(new IdleDetector(PlatformUI.getWorkbench().getDisplay(), 10, 10).isUserActive());
	}

	@Test
	public void testSetRunning() {
		IdleDetector d = new IdleDetector(PlatformUI.getWorkbench().getDisplay(), 10, 10);
		Assert.assertFalse(d.isRunning());

		try {
			d.setRunning(false);
			d.setRunning(false);
		} catch (Exception e) {
			Assert.fail();
		}

		try {
			d.setRunning(true);
			d.setRunning(true);
		} catch (Exception e) {
			Assert.fail();
		}

		d.setRunning(true);
		Assert.assertTrue(d.isRunning());
		try {
			Assert.assertFalse(getTimer(d).isShutdown());
		} catch (Exception e) {
			Assert.fail();
		}

		d.setRunning(false);
		Assert.assertFalse(d.isRunning());
		try {
			Assert.assertTrue(getTimer(d).isShutdown());
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testNotRunningNoNotify() throws InterruptedException {
		// IdleDetector is not running, so no observers should be notified

		long idleInterval = 500;
		long runDelay = 10;
		IdleDetector d = new IdleDetector(bot.getDisplay(), idleInterval, runDelay);

		ObserverTester ob = new ObserverTester();
		d.addObserver(ob);
		d.setRunning(false);

		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));

		for (int i = 0; i < 3; i++) {
			bot.menu("Help").menu("About").click();
			bot.shell("About Eclipse Platform").activate();
			bot.button("OK").click();
		}

		Assert.assertEquals(0, ob.inactiveCount);
		Assert.assertEquals(0, ob.activeCount);
	}

	@Test
	public void testAccuracy_withMouseDown() throws InterruptedException {
		long idleInterval = 500;
		long runDelay = 10;
		IdleDetector d = new IdleDetector(bot.getDisplay(), idleInterval, runDelay);
		d.setRunning(true);

		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		Assert.assertFalse(d.isUserActive());

		bot.menu("Help").menu("About").click();
		bot.shell("About Eclipse Platform").activate();
		bot.button("OK").click();
		Assert.assertTrue(d.isUserActive());
	}

	@Test
	public void testAccuracy_withMouseDownAndObserver() throws InterruptedException {
		long idleInterval = 500;
		long runDelay = 10;
		IdleDetector d = new IdleDetector(bot.getDisplay(), idleInterval, runDelay);

		ObserverTester ob = new ObserverTester();
		d.addObserver(ob);
		d.setRunning(true);

		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		Assert.assertFalse(d.isUserActive());

		for (int i = 0; i < 3; i++) {
			bot.menu("Help").menu("About").click();
			bot.shell("About Eclipse Platform").activate();
			bot.button("OK").click();
		}

		Assert.assertEquals(1, ob.inactiveCount);
		Assert.assertEquals(1, ob.activeCount);
	}

	@Test
	public void testAccuracy_withKeyDown() throws Exception {
		bot.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					page.openEditor(new FileEditorInput(getFileForTesting()), "org.eclipse.ui.DefaultTextEditor", true);
				} catch (Exception e) {
					Assert.fail();
				}
			}
		});
		SWTBotEclipseEditor editor = bot.activeEditor().toTextEditor();

		long idleInterval = 500;
		long runDelay = 10;
		IdleDetector d = new IdleDetector(bot.getDisplay(), idleInterval, runDelay);
		d.setRunning(true);

		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		Assert.assertFalse(d.isUserActive());

		editor.typeText("1");
		Assert.assertTrue(d.isUserActive());
	}

	@Test
	public void testAccuracy_withKeyDownAndObserver() throws InterruptedException {
		bot.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					page.openEditor(new FileEditorInput(getFileForTesting()), "org.eclipse.ui.DefaultTextEditor", true);
				} catch (Exception e) {
					Assert.fail();
				}
			}
		});
		SWTBotEclipseEditor editor = bot.activeEditor().toTextEditor();

		long idleInterval = 500;
		long runDelay = 10;
		IdleDetector d = new IdleDetector(bot.getDisplay(), idleInterval, runDelay);

		ObserverTester ob = new ObserverTester();
		d.addObserver(ob);
		d.setRunning(true);

		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		TimeUnit.MILLISECONDS.sleep(idleInterval + (runDelay * 2));
		Assert.assertFalse(d.isUserActive());

		editor.typeText("1");
		editor.typeText("2");

		Assert.assertEquals(1, ob.inactiveCount);
		Assert.assertEquals(1, ob.activeCount);
	}

	@Test
	public void testDisplayDisposed() {
		Display display = PlatformUI.createDisplay();
		IdleDetector d = new IdleDetector(display, 10, 10);
		display.dispose();

		try {
			d.setRunning(true);
			d.setRunning(false);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	private ScheduledThreadPoolExecutor getTimer(IdleDetector d) throws Exception {
		Field field = d.getClass().getDeclaredField("timer");
		field.setAccessible(true);
		return (ScheduledThreadPoolExecutor) field.get(d);
	}

	private IFile getFileForTesting() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("Tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		IFile file = project.getFile("hello.txt");
		if (!file.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp", "txt"));
			file.create(stream, false, null);
			stream.close();
		}
		return file;
	}

	private static class ObserverTester implements Observer {
		private int activeCount = 0;
		private int inactiveCount = 0;

		@Override
		public void update(Observable o, Object arg) {
			IdleDetector detect = (IdleDetector) o;
			if (detect.isUserActive()) {
				activeCount++;
			} else {
				inactiveCount++;
			}
		}
	}
}
