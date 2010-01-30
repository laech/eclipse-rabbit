package rabbit.tracking.trackers;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;

import rabbit.tracking.event.CommandEvent;


public class CommandTrackerTest extends TrackerTest<CommandEvent> {
	
	private CommandTracker tracker = createTracker();
	
	@Test
	public void testAccuracy() {
		
		String commandId = "abc.cef.godwjcnf";
		ExecutionEvent ee = createExecutionEvent(commandId);
		
		tracker.setEnabled(true);
		assertTrue(tracker.getData().isEmpty());
		Calendar start = Calendar.getInstance();
		tracker.preExecute(commandId, ee);
		Calendar end = Calendar.getInstance();
		assertEquals(1, tracker.getData().size());
		
		CommandEvent e = tracker.getData().iterator().next();
		assertEquals(ee, e.getExecutionEvent());
		assertTrue(start.compareTo(e.getTime()) <= 0);
		assertTrue(end.compareTo(e.getTime()) >= 0);
	}

	@Override
	protected CommandTracker createTracker() {
		return new CommandTracker();
	}

	@Override
	protected CommandEvent createEvent() {
		return new CommandEvent(Calendar.getInstance(), createExecutionEvent("1"));
	}
	
	private ExecutionEvent createExecutionEvent(String commandId) {
		return new ExecutionEvent(getCommandService()
				.getCommand(commandId), Collections.EMPTY_MAP, null, null);
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
}
