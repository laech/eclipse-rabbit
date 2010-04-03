package rabbit.data.test.xml.util;

import rabbit.data.internal.xml.util.StringUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see StringUtil
 */
public class StringUtilTest {

  @Test
  public void testAreEqual() {
    assertTrue(StringUtil.areEqual(null, null));
    assertTrue(StringUtil.areEqual("", null));
    assertTrue(StringUtil.areEqual(null, ""));
    assertTrue(StringUtil.areEqual("abc", "abc"));
    assertFalse(StringUtil.areEqual("abc", null));
  }

  @Test
  public void testGetString() {
    assertEquals("", StringUtil.getString(null));
  }

}
