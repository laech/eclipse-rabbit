package rabbit.data.test.access.model;

import rabbit.data.access.model.DateDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see DateDescriptor
 */
public class DateDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_null() {
    createDescriptor(null);
  }
  
  @Test
  public void testHashCode() {
    LocalDate date = new LocalDate();
    assertEquals(date.hashCode(), createDescriptor(date).hashCode());
  }
  
  @Test
  public void testEquals() {
    LocalDate date = new LocalDate();
    DateDescriptor des1 = createDescriptor(date);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));
    assertFalse(des1.equals(createDescriptor(date.plusDays(1))));
    assertTrue(des1.equals(createDescriptor(date)));
  }

  @Test
  public void testGetDate() {
    LocalDate date = new LocalDate();
    DateDescriptor des = createDescriptor(date);
    assertEquals(date, des.getDate());
  }

  /**
   * Creates a descriptor for testing. Subclass should override.
   * 
   * @param date The date will be used to create the descriptor.
   * @return A descriptor created using the date.
   */
  protected DateDescriptor createDescriptor(LocalDate date) {
    return new DateDescriptor(date);
  }
}
