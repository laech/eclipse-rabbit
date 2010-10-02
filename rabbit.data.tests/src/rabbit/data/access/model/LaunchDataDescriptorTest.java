package rabbit.data.access.model;

import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @see LaunchDataDescriptor
 */
public class LaunchDataDescriptorTest extends DateDescriptorTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_countNegative() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), -1,
        new Duration(1), Collections.<IPath> emptySet());
  }

  @Test
  public void testConstructor_countZero() {
    try {
      createDescriptor(new LocalDate(), createLaunchConfiguration(), 0,
          new Duration(1), Collections.<IPath> emptySet());
    } catch (IllegalArgumentException e) {
      fail("0 should be accepted");
    }
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_durationNull() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), 1, null,
        Collections.<IPath> emptySet());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_fileIdsNull() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), 1,
        new Duration(2), null);
  }

  @Test(expected = NullPointerException.class)
  public void testCosntructor_launchConfigNull() {
    createDescriptor(new LocalDate(), null, 2, new Duration(2),
        Collections.<IPath> emptySet());
  }

  @Test
  public void testFindFiles() {
    IPath validFilePath = new Path("/project/file.txt");
    IPath invalidFilePath = new Path("/");

    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), 1, new Duration(1), Arrays.asList(
            validFilePath, invalidFilePath));

    // The file with invalid path should be ignored, so the set should only
    // contain one file with the valid path.
    Set<IFile> files = des.findFiles();
    assertEquals(1, files.size());
    assertEquals(validFilePath, files.iterator().next().getFullPath());
  }

  @Test
  public void testGetFileIds() {
    ImmutableSet<IPath> filePaths = ImmutableSet.<IPath> of(new Path("/a"),
        new Path("/b"));
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), 1, new Duration(3), filePaths);
    assertEquals(filePaths, des.getFilePaths());
  }

  @Test
  public void testGetLaunchConfigurationDescriptor() {
    LaunchConfigurationDescriptor config = createLaunchConfiguration();
    LaunchDataDescriptor des = createDescriptor(new LocalDate(), config, 1,
        new Duration(1), Collections.<IPath> emptySet());
    assertEquals(config, des.getLaunchDescriptor());
  }

  @Test
  public void testGetLaunchCount() {
    int count = 2387;
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), count, new Duration(1),
        Collections.<IPath> emptySet());
    assertEquals(count, des.getLaunchCount());
  }

  @Test
  public void testGetTotalDuration() {
    Duration duration = new Duration(98238734);
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), 1, duration,
        Collections.<IPath> emptySet());
    assertEquals(duration, des.getDuration());
  }

  @Override
  protected final LaunchDataDescriptor createDescriptor(LocalDate date) {
    return createDescriptor(date, createLaunchConfiguration(), 1, new Duration(
        2), Collections.<IPath> emptySet());
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date.
   * @param des The launch configuration descriptor.
   * @param count The launch count.
   * @param duration The launch duration.
   * @param filePaths The file paths.
   * @return A descriptor created using the parameters.
   */
  protected LaunchDataDescriptor createDescriptor(LocalDate date,
      LaunchConfigurationDescriptor des, int count, Duration duration,
      Iterable<IPath> filePaths) {

    return new LaunchDataDescriptor(date, des, count, duration, filePaths);
  }

  private LaunchConfigurationDescriptor createLaunchConfiguration() {
    return new LaunchConfigurationDescriptor("1", "2", "3");
  }

}
