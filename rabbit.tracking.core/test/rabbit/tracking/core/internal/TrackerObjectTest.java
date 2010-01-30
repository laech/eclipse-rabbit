package rabbit.tracking.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import rabbit.tracking.core.ITracker;
import rabbit.tracking.core.TestUtil;

public class TrackerObjectTest {
	
	private String id = "idA";
	private String name = "nameA";
	private String description = "descA";
	private ITracker<?> tracker = TestUtil.newTracker();
	
	private TrackerObject tObject = new TrackerObject(id, name, description, tracker);

	@Test
	public void testTrackerObject() {
		assertNotNull(tObject);
	}

	@Test
	public void testGetId() {
		assertEquals(id, tObject.getId());
	}

	@Test
	public void testSetId() {
		
		String newId = "asdfkljsdlfj";
		tObject.setId(newId);
		assertEquals(newId, tObject.getId());
	}

	@Test
	public void testGetName() {
		assertEquals(name, tObject.getName());
	}

	@Test
	public void testSetName() {
		
		String newName = "`12434nvd";
		tObject.setName(newName);
		assertEquals(newName, tObject.getName());
	}

	@Test
	public void testGetDescription() {
		assertEquals(description, tObject.getDescription());
	}

	@Test
	public void testSetDescription() {
		
		String newDes = "HelloWorld...!";
		tObject.setDescription(newDes);
		assertEquals(newDes, tObject.getDescription());
	}

	@Test
	public void testGetTracker() {
		assertSame(tracker, tObject.getTracker());
	}

	@Test
	public void testSetTracker() {
		
		ITracker<?> newTracker = TestUtil.newTracker();
		tObject.setTracker(newTracker);
		assertSame(newTracker, tObject.getTracker());
	}

}
