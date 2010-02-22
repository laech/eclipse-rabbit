package rabbit.core.internal.trackers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.Before;
import org.junit.Test;

import rabbit.core.events.CommandEvent;

/**
 * Test for {@link CommandTracker}
 */
public class CommandTrackerTest extends AbstractTrackerTest<CommandEvent> {

	private CommandTracker tracker;

	@Before
	public void setUp() {
		tracker = createTracker();
	}

	@Test
	public void testExecution() throws Exception {
		tracker.setEnabled(true);

		String name = "cmdName";
		String description = "cmdDescription";
		Command command = getCommandService().getCommand(System.currentTimeMillis() + "." + System.nanoTime());
		command.define(name, description, getCommandService().getDefinedCategories()[0]);

		long start = System.currentTimeMillis();
		getHandlerService().activateHandler(command.getId(), createHandler());
		getHandlerService().executeCommand(command.getId(), null);
		long end = System.currentTimeMillis();

		assertEquals(1, tracker.getData().size());
		CommandEvent e = tracker.getData().iterator().next();
		assertEquals(command, e.getExecutionEvent().getCommand());
		assertTrue(start <= e.getTime().getTimeInMillis());
		assertTrue(end >= e.getTime().getTimeInMillis());
	}

	@Test
	public void testDisabled() throws Exception {
		tracker.setEnabled(false);

		Command command = getCommandService().getCommand(System.currentTimeMillis() + "." + System.nanoTime());
		command.define("a", "b", getCommandService().getDefinedCategories()[0]);

		getHandlerService().activateHandler(command.getId(), createHandler());
		getHandlerService().executeCommand(command.getId(), null);

		assertTrue(tracker.getData().isEmpty());
	}

	@Override
	protected CommandTracker createTracker() {
		return new CommandTracker();
	}

	@Override
	protected CommandEvent createEvent() {
		return new CommandEvent(Calendar.getInstance(), createExecutionEvent("1"));
	}

	private IHandler createHandler() {
		return new AbstractHandler() {
			@Override
			public Object execute(ExecutionEvent event) throws ExecutionException {
				return null;
			}
		};
	}

	private ExecutionEvent createExecutionEvent(String commandId) {
		return new ExecutionEvent(getCommandService().getCommand(commandId), Collections.EMPTY_MAP, null, null);
	}

	private IHandlerService getHandlerService() {
		return (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
	}

	private ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
	}
}
