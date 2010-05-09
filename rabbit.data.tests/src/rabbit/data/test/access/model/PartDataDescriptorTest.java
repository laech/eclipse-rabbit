package rabbit.data.test.access.model;

import rabbit.data.access.model.PartDataDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see PartDataDescriptor
 */
public class PartDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_partIdNull() {
    createDescriptor(new LocalDate(), 2, null);
  }

  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    long value = 98213;
    String id = "a.id";
    int hashCode = Objects.hashCode(date, id);
    assertEquals(hashCode, createDescriptor(date, value, id).hashCode());
  }

  @Override
  public void testEquals() {
    LocalDate date = new LocalDate();
    long value = 98213;
    String id = "a.id";

    PartDataDescriptor des1 = createDescriptor(date, value, id);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));

    PartDataDescriptor des2 = createDescriptor(date, value, id);
    assertTrue(des1.equals(des2));

    des2 = createDescriptor(date.plusDays(1), value, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, 1 + value, id);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, value, id + '.');
    assertFalse(des1.equals(des2));
  }
  
  @Test
  public void testFindPart() {
    // A valid editor ID:
    String partId = "org.eclipse.ui.DefaultTextEditor";
    assertEquals(partId, createDescriptor(new LocalDate(), 1, partId).findPart()
        .getId());
    
    // A valid view ID:
    partId = "org.eclipse.ui.navigator.ProjectExplorer";
    assertEquals(partId, createDescriptor(new LocalDate(), 1, partId).findPart()
        .getId());
    
    // An invalid ID:
    partId = "not.exist";
    assertNull(createDescriptor(new LocalDate(), 1, partId).findPart());
  }

  @Test
  public void testGetPartId() {
    String id = "a.part.id";
    assertEquals(id, createDescriptor(new LocalDate(), 1, id).getPartId());
  }

  @Override
  protected final PartDataDescriptor createDescriptor(LocalDate date, long value) {
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
  protected PartDataDescriptor createDescriptor(LocalDate date, long value,
      String partId) {
    return new PartDataDescriptor(date, value, partId);
  }
}
