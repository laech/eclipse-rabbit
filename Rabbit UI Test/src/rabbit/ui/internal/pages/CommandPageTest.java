package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.CommandDataAccessor;
import rabbit.ui.DisplayPreference;

/**
 * Test for {@link CommandPage}
 */
public class CommandPageTest extends AbstractTableViewerPageTest {

	@Override
	protected AbstractTableViewerPage createPage() {
		return new CommandPage();
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor accessor = new CommandDataAccessor();

		DisplayPreference pref = new DisplayPreference();
		Map<String, Long> data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());

		pref.getStartDate().add(Calendar.MONTH, -1);
		pref.getEndDate().add(Calendar.DAY_OF_MONTH, -5);
		data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		max = 0;
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValue() throws Exception {
		Field field = CommandPage.class.getDeclaredField("dataMapping");
		field.setAccessible(true);
		Map<Command, Long> data = (Map<Command, Long>) field.get(page);

		Command command = getCommandService().getDefinedCommands()[0];
		long value = 1989;
		data.put(command, value);
		assertEquals(value, page.getValue(command));

		Command noValueCommand = getCommandService().getCommand(System.currentTimeMillis() + "");
		assertEquals(0, page.getValue(noValueCommand));
	}

	private static ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
	}

}
