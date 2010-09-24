package rabbit.tracking.tests.util;

import rabbit.tracking.internal.util.Recorder.Record;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @see Record
 */
public class RecordTest {

  @Test
  public void testConstructor_nullUserData() {
    new Record(0, 1, null); // No exception.
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testContructor_endLessThanStart() {
    new Record(1, 0, "");
  }
  
  @Test
  public void testContructor_equalStartAndEnd() {
    new Record(1, 1, ""); // No exception.
  }
  
  @Test
  public void testGetEndTimeMillis() {
    assertEquals(8, new Record(7, 8, null).getEndTimeMillis());
  }
  
  @Test
  public void testGetStartTimeMillis() {
    assertEquals(7, new Record(7, 8, null).getStartTimeMillis());
  }
  
  @Test
  public void testGetUserData() {
    assertEquals(this, new Record(0, 1, this).getUserData());
  }
}
