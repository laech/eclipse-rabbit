package rabbit.data.test.access.model;

import rabbit.data.access.model.ValueDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see ValueDescriptor
 */
public class ValueDescriptorTest extends DateDescriptorTest {

  @Test
  public void testConstructor_value_zero() {
    try {
      createDescriptor(new LocalDate(), 0);
    } catch (IllegalArgumentException e) {
      fail("0 should be accepted.");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstuctor_value_negative() {
    createDescriptor(new LocalDate(), -1);
  }
  
  @Test
  public void testGetValue() {
    long value = 1098;
    ValueDescriptor des = createDescriptor(new LocalDate(), value);
    assertEquals(value, des.getValue());
  }
  
  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    long value = 1000;
    int hashCode = Objects.hashCode(date);
    assertEquals(hashCode, createDescriptor(date, value).hashCode());
  }
  
  @Override
  public void testEquals() {
    LocalDate date = new LocalDate();
    long value = 1000;
    
    ValueDescriptor des1 = createDescriptor(date, value);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals("m"));
    
    ValueDescriptor des2 = createDescriptor(date, value);
    assertTrue(des1.equals(des2));
    
    des2 = createDescriptor(date.plusDays(1), value);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, value + 1);
    assertFalse(des1.equals(des2));
  }

  @Override
  protected final ValueDescriptor createDescriptor(LocalDate date) {
    return createDescriptor(date, 1);
  }

  /**
   * Creates a descriptor for testing. Subclass should override.
   * 
   * @param date The date will be used to create the descriptor.
   * @param value The value will be used to create the descriptor.
   * @return A descriptor created using the parameters.
   */
  protected ValueDescriptor createDescriptor(LocalDate date, long value) {
    return new ValueDescriptor(date, value);
  }
}
