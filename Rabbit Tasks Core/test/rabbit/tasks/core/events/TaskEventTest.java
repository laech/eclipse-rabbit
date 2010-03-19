package rabbit.tasks.core.events;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

/**
 * @see TaskEvent
 */
@SuppressWarnings("restriction")
public class TaskEventTest {

	@Test(expected = NullPointerException.class)
	public void testConstructor_taskNull() {
		new TaskEvent(Calendar.getInstance(), 1, "Abc", null);
	}

	@Test
	public void testGetTask() {
		ITask task = new LocalTask("abc", "def");
		assertEquals(task, new TaskEvent(
				Calendar.getInstance(), 1, "abcd", task).getTask());
	}

	@Test
	public void testSetTask() {
		ITask task = new LocalTask("abc", "def");
		TaskEvent event = new TaskEvent(Calendar.getInstance(), 1, "abcd", task);

		task = new LocalTask("124", "567");
		event.setTask(task);
		assertEquals(task, event.getTask());
	}

	@Test(expected = NullPointerException.class)
	public void testSetTask_null() {
		ITask task = new LocalTask("abc", "def");
		new TaskEvent(Calendar.getInstance(), 1, "abcd", task).setTask(null);
	}
}
