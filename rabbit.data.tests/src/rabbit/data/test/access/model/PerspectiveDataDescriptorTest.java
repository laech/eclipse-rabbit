package rabbit.data.test.access.model;

import rabbit.data.access.model.PerspectiveDataDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see PerspectiveDataDescriptor
 */
public class PerspectiveDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_perspectiveIdNull() {
    createDescriptor(new LocalDate(), 1, null);
  }
  
  @Test
  public void testFindPerspective() {
    // A valid perspective ID:
    String id = "org.eclipse.ui.resourcePerspective";
    assertEquals(id, createDescriptor(new LocalDate(), 1, id).findPerspective().getId());
    
    // An invalid perspective ID:
    id = "not.exist";
    assertNull(createDescriptor(new LocalDate(), 2, id).findPerspective());
  }

  @Test
  public void testGetPerspectiveId() {
    String id = "perspective.id";
    assertEquals(id, createDescriptor(new LocalDate(), 1, id)
        .getPerspectiveId());
  }

  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    long duration = 11;
    String id = "id";
    int hashCode = Objects.hashCode(date, id);
    assertEquals(hashCode, createDescriptor(date, duration, id).hashCode());
  }

  @Override
  public void testEquals() {
    LocalDate date = new LocalDate();
    long duration = 11;
    String id = "id";

    PerspectiveDataDescriptor des1 = createDescriptor(date, duration, id);
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));
    assertTrue(des1.equals(des1));

    PerspectiveDataDescriptor des2 = createDescriptor(date, duration, id);
    assertTrue(des1.equals(des2));

    des2 = createDescriptor(date.plusDays(1), duration, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, duration + 1, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, duration, id + ".");
    assertFalse(des1.equals(des2));
  }

  @Override
  protected final PerspectiveDataDescriptor createDescriptor(LocalDate date,
      long value) {
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
      long value, String perspectiveId) {
    return new PerspectiveDataDescriptor(date, value, perspectiveId);
  }
}
