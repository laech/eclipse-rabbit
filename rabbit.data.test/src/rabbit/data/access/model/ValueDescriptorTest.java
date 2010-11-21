package rabbit.data.access.model;

import rabbit.data.access.model.DurationDescriptor;

import static org.junit.Assert.assertEquals;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see DurationDescriptor
 */
public class ValueDescriptorTest extends DateDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_durationNull() {
    createDescriptor(new LocalDate(), null);
  }

  @Test
  public void testGetValue() {
    Duration duration = new Duration(10030);
    DurationDescriptor des = createDescriptor(new LocalDate(), duration);
    assertEquals(duration, des.getDuration());
  }

  @Override
  protected final DurationDescriptor createDescriptor(LocalDate date) {
    return createDescriptor(date, new Duration(12));
  }

  /**
   * Creates a descriptor for testing. Subclass should override.
   * 
   * @param date The date will be used to create the descriptor.
   * @param duration The duration.
   * @return A descriptor created using the parameters.
   */
  protected DurationDescriptor createDescriptor(LocalDate date,
      Duration duration) {
    return new DurationDescriptor(date, duration);
  }
}
