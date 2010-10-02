package rabbit.data.test.access.model;

import rabbit.data.access.model.PerspectiveDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see PerspectiveDataDescriptor
 */
public class PerspectiveDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_perspectiveIdNull() {
    createDescriptor(new LocalDate(), new Duration(1), null);
  }

  @Test
  public void testFindPerspective() {
    // A valid perspective ID:
    String id = "org.eclipse.ui.resourcePerspective";
    assertEquals(
        id,
        createDescriptor(new LocalDate(), new Duration(1), id).findPerspective().getId());

    // An invalid perspective ID:
    id = "not.exist";
    assertNull(createDescriptor(new LocalDate(), new Duration(2), id).findPerspective());
  }

  @Test
  public void testGetPerspectiveId() {
    String id = "perspective.id";
    assertEquals(
        id,
        createDescriptor(new LocalDate(), new Duration(1), id).getPerspectiveId());
  }

  @Override
  protected final PerspectiveDataDescriptor createDescriptor(LocalDate date,
      Duration value) {
    return createDescriptor(date, value, "a.id");
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param value The value will be used to create the descriptor.
   * @param perspective The ID of a perspective.
   * @return A descriptor created using the parameters.
   */
  protected PerspectiveDataDescriptor createDescriptor(LocalDate date,
      Duration value, String perspectiveId) {
    return new PerspectiveDataDescriptor(date, value, perspectiveId);
  }
}
