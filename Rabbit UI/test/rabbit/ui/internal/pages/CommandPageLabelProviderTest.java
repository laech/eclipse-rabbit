package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link CommandPageLabelProvider}
 */
public class CommandPageLabelProviderTest {

	private static Shell shell;
	private static CommandPage page;
	private static CommandPageLabelProvider provider;

	private static Command definedCommand;
	private static Command undefinedCommand;

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new CommandPage();
		page.createContents(shell);
		provider = new CommandPageLabelProvider(page);
		definedCommand = getCommandService().getDefinedCommands()[0];
		undefinedCommand = getCommandService().getCommand(System.currentTimeMillis() + "");
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@Test
	public void testGetColumnText() throws Exception {
		Map<Command, Long> data = getDataMap(page);

		long definedValue = 1000;
		data.put(definedCommand, definedValue);

		long undefinedValue = 19489;
		data.put(undefinedCommand, undefinedValue);

		page.getViewer().setInput(data.keySet());

		assertEquals(definedCommand.getName(), provider.getColumnText(definedCommand, 0));
		assertEquals(definedCommand.getDescription(), provider.getColumnText(definedCommand, 1));
		assertEquals(String.valueOf(definedValue), provider.getColumnText(definedCommand, 2));

		assertEquals(undefinedCommand.getId(), provider.getColumnText(undefinedCommand, 0));
		assertNull(provider.getColumnText(undefinedCommand, 1));
		assertEquals(String.valueOf(undefinedValue), provider.getColumnText(undefinedCommand, 2));
	}

	@Test
	public void testGetColumnImage() {
		assertNull(provider.getColumnImage(definedCommand, 0));
		assertNull(provider.getColumnImage(definedCommand, 1));
		assertNull(provider.getColumnImage(definedCommand, 2));
		assertNull(provider.getColumnImage(undefinedCommand, 0));
		assertNull(provider.getColumnImage(undefinedCommand, 1));
		assertNull(provider.getColumnImage(undefinedCommand, 2));
	}

	@Test
	public void testGetForeground() {
		assertNull(provider.getForeground(definedCommand));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider.getForeground(undefinedCommand));
	}

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(definedCommand));
		assertNull(provider.getBackground(undefinedCommand));
	}

	@SuppressWarnings("unchecked")
	private Map<Command, Long> getDataMap(CommandPage page) throws Exception {
		Field field = CommandPage.class.getDeclaredField("dataMapping");
		field.setAccessible(true);
		return (Map<Command, Long>) field.get(page);
	}

	private static ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
	}
}
