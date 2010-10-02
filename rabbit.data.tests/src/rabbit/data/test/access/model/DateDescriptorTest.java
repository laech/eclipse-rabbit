package rabbit.data.test.access.model;

import rabbit.data.access.model.DateDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
  public void testGetDate() {
    LocalDate date = new LocalDate();
    DateDescriptor des = createDescriptor(date);
    assertEquals(date, des.getDate());
  }

  @Test
  public void testEquals() {
    // Each instance is meant to be unique, otherwise some data will be lost
    // when storing in hash collections:
    LocalDate date = new LocalDate();
    assertFalse(createDescriptor(date).equals(
        createDescriptor(date)));
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
