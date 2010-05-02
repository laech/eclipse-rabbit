package rabbit.data.test.access.model;

import rabbit.data.access.model.FileDataDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
    IPath path = new Path("/p/f/a");
    LocalDate date = new LocalDate();
    int hashCode = Objects.hashCode(date, path);
    assertEquals(hashCode, createDescriptor(date, val, path).hashCode());
  }
  
  @Override
  public void testEquals() {
    long val = 1983;
    IPath path = new Path("/p/f/a");
    LocalDate date = new LocalDate();
    
    FileDataDescriptor des1 = createDescriptor(date, val, path);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));
    
    FileDataDescriptor des2 = createDescriptor(date, val, path);
    assertTrue(des1.equals(des2));
    assertTrue(des2.equals(des1));
    
    des2 = createDescriptor(date.plusDays(1), val, path);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, val + 1, path);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, val, path.append("1"));
    assertFalse(des1.equals(des2));
  }

  @Test
  public void testGetFileId() {
    IPath path = new Path("/p/f/a");
    assertEquals(path, createDescriptor(new LocalDate(), 1, path).getFilePath());
  }

  @Override
  protected final FileDataDescriptor createDescriptor(LocalDate date, long duration) {
    return createDescriptor(date, duration, Path.fromPortableString("/p/f/a.txt"));
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date will be used to create the descriptor.
   * @param duration The duration will be used to create the descriptor.
   * @param fileId The file path will be used to create the descriptor.
   * @return A descriptor created using the parameters.
   * @see FileDataDescriptor#FileDataDescriptor(LocalDate, long, String)
   */
  protected FileDataDescriptor createDescriptor(LocalDate date, long duration,
      IPath filePath) {
    return new FileDataDescriptor(date, duration, filePath);
  }

}
