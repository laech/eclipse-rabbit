package rabbit.data.test.access.model;

import rabbit.data.access.model.SessionDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;

/**
 * @see SessionDataDescriptor
 */
public class SessionDataDescriptorTest extends ValueDescriptorTest {

  @Override
  protected SessionDataDescriptor createDescriptor(LocalDate date, long value) {
    return new SessionDataDescriptor(date, value);
  }
  
  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    long value = 1000;
    int hashCode = (date.hashCode() + (int) value) % 31;
    assertEquals(hashCode, createDescriptor(date, value).hashCode());
  }
  
  @Override
  public void testEquals() {
    LocalDate date = new LocalDate();
    long value = 1000;
    
    SessionDataDescriptor des1 = createDescriptor(date, value);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals("m"));
    
    SessionDataDescriptor des2 = createDescriptor(date, value);
    assertTrue(des1.equals(des2));
    
    des2 = createDescriptor(date.plusDays(1), value);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, value + 1);
    assertFalse(des1.equals(des2));
  }
}
