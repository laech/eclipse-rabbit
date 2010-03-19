package rabbit.tasks.core;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @see TaskCore
 */
public class TaskCoreTest {

	@Test
	public void testGetTaskDataAccessor() {
		assertNotNull(TaskCore.getTaskDataAccessor());
	}
}
