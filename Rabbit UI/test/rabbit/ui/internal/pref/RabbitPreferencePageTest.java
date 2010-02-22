package rabbit.ui.internal.pref;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import rabbit.core.RabbitCore;

/**
 * Test for {@link RabbitPreferencePage}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RabbitPreferencePageTest {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTWorkbenchBot();
	}

	@Test
	public void testApply() {
		RabbitCore.getDefault().setIdleDetectionEnabled(false);
		openRabbitPreferences();

		SWTBotCheckBox idleFeature = bot.checkBox();
		assertFalse(idleFeature.isChecked());
		idleFeature.click();
		bot.button("Apply").click();
		assertTrue(RabbitCore.getDefault().isIdleDetectionEnabled());

		idleFeature.click();
		bot.button("Apply").click();
		assertFalse(RabbitCore.getDefault().isIdleDetectionEnabled());

		bot.activeShell().close();
	}

	@Test
	public void testOK() {
		RabbitCore.getDefault().setIdleDetectionEnabled(false);
		openRabbitPreferences();

		SWTBotCheckBox idleFeature = bot.checkBox();
		assertFalse(idleFeature.isChecked());

		idleFeature.click(); // Check it
		bot.button("OK").click();
		assertTrue(RabbitCore.getDefault().isIdleDetectionEnabled());

		openRabbitPreferences();
		idleFeature = bot.checkBox();
		idleFeature.click(); // Uncheck it
		bot.button("OK").click();
		assertFalse(RabbitCore.getDefault().isIdleDetectionEnabled());
	}

	@Test
	public void testDefaults() {
		RabbitCore.getDefault().setIdleDetectionEnabled(true);
		openRabbitPreferences();
		bot.button("Restore Defaults").click();
		assertFalse(bot.checkBox().isChecked());
	}

	private void openRabbitPreferences() {
		bot.menu("Window").menu("Preferences").click();
		bot.tree().select("Rabbit");
	}

}
