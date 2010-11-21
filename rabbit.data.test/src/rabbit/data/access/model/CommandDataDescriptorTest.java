package rabbit.data.access.model;

import rabbit.data.access.model.CommandDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see CommandDataDescriptor
 */
public class CommandDataDescriptorTest extends DateDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_commandIdNull() {
    createDescriptor(new LocalDate(), 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_commandIdEmpty() {
    createDescriptor(new LocalDate(), 1, "");
  }

  @Test
  public void testGetCount() {
    assertEquals(10,
        new CommandDataDescriptor(new LocalDate(), 10, "1").getCount());
  }

  @Test
  public void testFindCommand() {
    String id = "command.a.b.c";
    assertNotNull(createDescriptor(new LocalDate(), 1, id).findCommand());
  }

  @Test
  public void testGetCommandId() {
    String id = "command.a.id";
    assertEquals(id, createDescriptor(new LocalDate(), 2, id).getCommandId());
  }

  @Override
  protected final CommandDataDescriptor createDescriptor(LocalDate date) {
    return createDescriptor(date, 1, "anId");
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param count The count will be used to create the descriptor.
   * @param commandId The command ID will be used to create the descriptor.
   * @return A descriptor created using the parameters.
   */
  protected CommandDataDescriptor createDescriptor(LocalDate date, int count,
      String commandId) {
    return new CommandDataDescriptor(date, count, commandId);
  }
}
