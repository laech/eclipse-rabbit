package rabbit.core.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @see LaunchDescriptor
 */
public class LaunchDescriptorTest {

	private LaunchDescriptor descriptor;

	@Before
	public void before() {
		descriptor = new LaunchDescriptor();
	}

	@Test
	public void testEquals() {
		long duration = 102;
		Calendar launchTime = Calendar.getInstance();
		Set<String> fileIds = new HashSet<String>(Arrays.asList("1", "2"));
		String launchMode = "debug";
		String launchName = "name";
		String launchType = "type";

		LaunchDescriptor des1 = new LaunchDescriptor();
		LaunchDescriptor des2 = new LaunchDescriptor();
		assertTrue("Should be equal when first created.",
				des1.equals(des2));

		des1.setDuration(duration);
		assertFalse("Should not be equal when fields are different.",
				des1.equals(des2));

		des1.setFileIds(fileIds);
		des1.setLaunchModeId(launchMode);
		des1.setLaunchName(launchName);
		des1.getLaunchTime().setTimeInMillis(launchTime.getTimeInMillis());
		des1.setLaunchTypeId(launchType);
		assertFalse("Should not be equal when fields are different.",
				des1.equals(des2));

		des2.setDuration(duration);
		des2.setFileIds(fileIds);
		des2.setLaunchModeId(launchMode);
		des2.setLaunchName(launchName);
		des2.getLaunchTime().setTimeInMillis(launchTime.getTimeInMillis());
		des2.setLaunchTypeId(launchType);
		assertTrue("Should be equal when all fields are the same.",
				des1.equals(des2));
	}

	@Test
	public void testGetDuration() {
		assertEquals("Duration should be 0 by default.",
				0, descriptor.getDuration());
	}

	@Test
	public void testGetFileIds() {
		assertNotNull("File IDs should not be null.",
				descriptor.getFileIds());

		assertTrue("File IDs should be empty by default.",
				descriptor.getFileIds().isEmpty());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetFileIds_unmodifiable() {
		descriptor.getFileIds().add("A");
	}

	@Test
	public void testGetLaunchMode() {
		assertNotNull("Launch mode should not be null.",
				descriptor.getLaunchModeId());

		assertSame("Launch mode should be empty string by default",
				"", descriptor.getLaunchModeId());
	}

	@Test
	public void testGetLaunchName() {
		assertEquals("Launch name should be empty string by default.",
				"", descriptor.getLaunchName());
	}

	@Test
	public void testGetLaunchTime() {
		assertEquals("Launch time should be at 0 by default.",
				new GregorianCalendar(0, 0, 0), descriptor.getLaunchTime());
	}

	/*
	 * Test the calendar object returned can be modified to reflect the changes.
	 */
	@Test
	public void testGetLaunchTime_modifiable() {
		Calendar cal = descriptor.getLaunchTime();
		cal.add(Calendar.YEAR, 10);
		assertEquals(cal, descriptor.getLaunchTime());
	}

	@Test
	public void testGetLaunchType() {
		assertEquals("Launch type should be empty string by default.",
				"", descriptor.getLaunchTypeId());
	}

	@Test
	public void testHashCode() {
		int hashCode = (descriptor.getFileIds().hashCode()
				+ descriptor.getLaunchName().hashCode()
				+ descriptor.getLaunchTypeId().hashCode()
				+ descriptor.getLaunchModeId().hashCode()
				+ descriptor.getLaunchTime().hashCode())
				% 31;
		assertEquals(hashCode, descriptor.hashCode());
	}

	@Test
	public void testSetDuration() {
		long duration = System.currentTimeMillis();
		descriptor.setDuration(duration);
		assertEquals(duration, descriptor.getDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetDuration_negative() {
		descriptor.setDuration(-1);
	}

	@Test
	public void testSetDuration_zero() {
		try {
			descriptor.setDuration(0);
		} catch (Exception e) {
			fail("Should not fail when setting to 0.");
		}
	}

	@Test
	public void testSetFileIds() {
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("1");
		fileIds.add("2");
		fileIds.add("3");
		descriptor.setFileIds(fileIds);
		assertTrue(fileIds.containsAll(descriptor.getFileIds()));
		assertTrue(descriptor.getFileIds().containsAll(fileIds));
	}

	@Test
	public void testSetFileIds_copiesIds() {
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("1");
		fileIds.add("2");
		fileIds.add("3");
		descriptor.setFileIds(fileIds);

		fileIds.clear();
		assertFalse("File IDs should not be effect by external changes.",
				descriptor.getFileIds().isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void testSetFileIds_null() {
		descriptor.setFileIds(null);
	}

	@Test
	public void testSetLaunchMode() {
		String mode = "run";
		descriptor.setLaunchModeId(mode);
		assertEquals(mode, descriptor.getLaunchModeId());

		mode = "debug";
		descriptor.setLaunchModeId(mode);
		assertEquals(mode, descriptor.getLaunchModeId());
	}

	@Test(expected = NullPointerException.class)
	public void testSetLaunchMode_null() {
		descriptor.setLaunchModeId(null);
	}

	@Test
	public void testSetLaunchName() {
		String name = "adfasdf244";
		descriptor.setLaunchName(name);
		assertEquals(name, descriptor.getLaunchName());

		name = "asdfjh237";
		descriptor.setLaunchName(name);
		assertEquals(name, descriptor.getLaunchName());
	}

	@Test(expected = NullPointerException.class)
	public void testSetLaunchName_null() {
		descriptor.setLaunchName(null);
	}
	
	@Test
	public void testSetLaunchType() {
		String type = "adfjh298f";
		descriptor.setLaunchTypeId(type);
		assertEquals(type, descriptor.getLaunchTypeId());

		type = "987324iuyfjsdg";
		descriptor.setLaunchTypeId(type);
		assertEquals(type, descriptor.getLaunchTypeId());
	}
	
	@Test(expected = NullPointerException.class)
	public void testSetLaunchType_null() {
		descriptor.setLaunchTypeId(null);
	}
}