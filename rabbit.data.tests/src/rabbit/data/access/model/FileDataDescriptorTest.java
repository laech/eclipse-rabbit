package rabbit.data.access.model;

import rabbit.data.access.model.FileDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see FileDataDescriptor
 */
public class FileDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_fileIdNull() {
    createDescriptor(new LocalDate(), new Duration(1), null);
  }

  @Test
  public void testFindFile() {
    IPath path = new Path("/p/a.txt");
    assertEquals(
        path,
        createDescriptor(new LocalDate(), new Duration(1), path).findFile().getFullPath());

    path = new Path("/");
    assertNull(createDescriptor(new LocalDate(), new Duration(1), path).findFile());
  }

  @Test
  public void testGetFilePath() {
    IPath path = new Path("/p/f/a");
    assertEquals(path,
        createDescriptor(new LocalDate(), new Duration(1), path).getFilePath());
  }

  @Override
  protected final FileDataDescriptor createDescriptor(LocalDate date,
      Duration duration) {
    return createDescriptor(date, duration,
        Path.fromPortableString("/p/f/a.txt"));
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
  protected FileDataDescriptor createDescriptor(LocalDate date,
      Duration duration, IPath filePath) {
    return new FileDataDescriptor(date, duration, filePath);
  }

}
