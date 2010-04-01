package rabbit.core.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @see StringUtil
 */
public class StringUtilTest {

	@Test
	public void testGetString() {
		assertEquals("", StringUtil.getString(null));
	}

	@Test
	public void testAreEqual() {
		assertTrue(StringUtil.areEqual(null, null));
		assertTrue(StringUtil.areEqual("", null));
		assertTrue(StringUtil.areEqual(null, ""));
		assertTrue(StringUtil.areEqual("abc", "abc"));
		assertFalse(StringUtil.areEqual("abc", null));
	}

}
