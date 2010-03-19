package rabbit.tasks.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

/**
 * @see TaskId
 */
public class TaskIdTest {

	@Test(expected = NullPointerException.class)
	public void testConstructor_idNull() {
		new TaskId(null, new Date());
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructor_dateNull() {
		new TaskId("abc", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_emptyId() {
		new TaskId("", new Date());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_whiteSpaceId() {
		new TaskId(" \t", new Date());
	}
	
	@Test
	public void testGetHandleIdentifier() {
		String handleId = "helloWorld";
		TaskId taskId = new TaskId(handleId, new Date());
		assertEquals(handleId, taskId.getHandleIdentifier());
	}
	
	@Test
	public void testGetCreationDate() {
		Date creationDate = new Date();
		TaskId taskId = new TaskId("anId", creationDate);
		assertEquals(creationDate, taskId.getCreationDate());
	}
	
	@Test
	public void testHashCode() {
		String handleId = "ncikuhiuhfcs.sfghjowe";
		TaskId taskId = new TaskId(handleId, new Date());
		assertEquals(handleId.hashCode(), taskId.hashCode());
	}
	
	@Test
	public void testEquals() {
		String handleId = "n2u38fhcbcddf";
		Date creationDate = new Date();
		
		TaskId taskId1 = new TaskId(handleId, creationDate);
		TaskId taskId2 = new TaskId("rnvnh389fhvn", new Date());
		assertFalse(taskId1.equals(taskId2));
		
		taskId2 = new TaskId(handleId, new GregorianCalendar(1999, 1, 1).getTime());
		assertFalse(taskId1.equals(taskId2));
		
		taskId2 = new TaskId("nhe2834uhdfkj2938f", creationDate);
		assertFalse(taskId1.equals(taskId2));
		
		taskId2 = new TaskId(handleId, creationDate);
		assertTrue(taskId1.equals(taskId2));
	}
}
