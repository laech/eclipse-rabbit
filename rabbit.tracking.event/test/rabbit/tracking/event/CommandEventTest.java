package rabbit.tracking.event;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Calendar;
import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

public class CommandEventTest extends DiscreteEventTest {

	private ExecutionEvent exe = new ExecutionEvent(getCommandService()
			.getCommand("something"), Collections.EMPTY_MAP, null, null);

	private CommandEvent event = new CommandEvent(Calendar.getInstance(), exe);

	@Override
	protected CommandEvent createEvent(Calendar time) {
		return new CommandEvent(time, exe);
	}
	
	/**
	 * Gets the workbench command service.
	 * 
	 * @return The command service.
	 */
	private ICommandService getCommandService() {
		return (ICommandService) PlatformUI.getWorkbench().getService(
				ICommandService.class);
	}

	@Test
	public void testCommandEvent() {
		assertNotNull(event);
	}

	@Test
	public void testGetExecutionEvent() {
		assertSame(exe, event.getExecutionEvent());
	}

	@Test
	public void testSetExecutionEvent() {
		
		ExecutionEvent newExe = new ExecutionEvent(getCommandService()
				.getCommand("blah"), Collections.EMPTY_MAP, null, null);
		event.setExecutionEvent(newExe);
		assertSame(newExe, event.getExecutionEvent());
	}
}
