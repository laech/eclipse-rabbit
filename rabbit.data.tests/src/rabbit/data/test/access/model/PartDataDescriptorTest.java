package rabbit.data.test.access.model;

import rabbit.data.access.model.PartDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see PartDataDescriptor
 */
public class PartDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_partIdNull() {
    createDescriptor(new LocalDate(), new Duration(2), null);
  }

  @Test
  public void testFindPart() {
    // A valid editor ID:
    String partId = "org.eclipse.ui.DefaultTextEditor";
    assertEquals(partId, createDescriptor(new LocalDate(), new Duration(1),
        partId).findPart().getId());

    // A valid view ID:
    partId = "org.eclipse.ui.navigator.ProjectExplorer";
    assertEquals(partId, createDescriptor(new LocalDate(), new Duration(1),
        partId).findPart().getId());

    // An invalid ID:
    partId = "not.exist";
    assertNull(createDescriptor(new LocalDate(), new Duration(1), partId).findPart());
  }

  @Test
  public void testGetPartId() {
    String id = "a.part.id";
    assertEquals(id,
        createDescriptor(new LocalDate(), new Duration(1), id).getPartId());
  }

  @Override
  protected final PartDataDescriptor createDescriptor(LocalDate date,
      Duration value) {
    return createDescriptor(date, value, "part.id");
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param value The value will be used to create the descriptor.
   * @param partId The ID of a workbench part.
   * @return A descriptor created using the parameters.
   */
  protected PartDataDescriptor createDescriptor(LocalDate date, Duration value,
      String partId) {
    return new PartDataDescriptor(date, value, partId);
  }
}
