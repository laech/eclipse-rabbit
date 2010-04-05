package rabbit.data.test.access.model;

import rabbit.data.access.model.FileDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see FileDataDescriptor
 */
public class FileDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_fileIdNull() {
    createDescriptor(new LocalDate(), 1, null);
  }

  @Override
  public void testHashCode() {
    long val = 1983;
    String id = "asd";
    LocalDate date = new LocalDate();
    int hashCode = (id.hashCode() + (int) val + date.hashCode()) % 31;
    assertEquals(hashCode, createDescriptor(date, val, id).hashCode());
  }
  
  @Override
  public void testEquals() {
    long val = 1983;
    String id = "asd";
    LocalDate date = new LocalDate();
    
    FileDataDescriptor des1 = createDescriptor(date, val, id);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));
    
    FileDataDescriptor des2 = createDescriptor(date, val, id);
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
  public void testGetFileId() {
    String id = "a.file.id.b";
    assertEquals(id, createDescriptor(new LocalDate(), 1, id).getFileId());
  }

  @Override
  protected FileDataDescriptor createDescriptor(LocalDate date, long duration) {
    return createDescriptor(date, duration, "fileId.x");
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param duration The duration will be used to create the descriptor.
   * @param fileId The file ID will be used to create the descriptor.
   * @return A descriptor created using the parameters.
   */
  protected FileDataDescriptor createDescriptor(LocalDate date, long duration,
      String fileId) {
    return new FileDataDescriptor(date, duration, fileId);
  }

}
