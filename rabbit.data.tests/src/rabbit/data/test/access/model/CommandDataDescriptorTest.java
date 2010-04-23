package rabbit.data.test.access.model;

import rabbit.data.access.model.CommandDataDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see CommandDataDescriptor
 */
public class CommandDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_commandIdNull() {
    createDescriptor(new LocalDate(), 1, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_commandIdEmpty() {
    createDescriptor(new LocalDate(), 1, "");
  }

  @Override
  public void testHashCode() {
    long val = 1983;
    String id = "asd";
    LocalDate date = new LocalDate();
    int hashCode = Objects.hashCode(date, id);
    assertEquals(hashCode, createDescriptor(date, val, id).hashCode());
  }

  @Override
  public void testEquals() {
    long val = 1983;
    String id = "asd";
    LocalDate date = new LocalDate();

    CommandDataDescriptor des1 = createDescriptor(date, val, id);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));

    CommandDataDescriptor des2 = createDescriptor(date, val, id);
    assertTrue(des1.equals(des2));
    assertTrue(des2.equals(des1));

    des2 = createDescriptor(date.plusDays(1), val, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, val + 1, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, val, id + "1");
    assertFalse(des1.equals(des2));
  }

  @Test
  public void testGetCommandId() {
    String id = "command.a.id";
    assertEquals(id, createDescriptor(new LocalDate(), 2, id).getCommandId());
  }

  @Override
  protected final CommandDataDescriptor createDescriptor(LocalDate date,
      long value) {
    return createDescriptor(date, value, "anId");
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param count The count will be used to create the descriptor.
   * @param commandId The command ID will be used to create the descriptor.
   * @return A descriptor created using the parameters.
   */
  protected CommandDataDescriptor createDescriptor(LocalDate date, long count,
      String commandId) {
    return new CommandDataDescriptor(date, count, commandId);
  }
}
