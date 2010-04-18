package rabbit.data.test.access.model;

import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @see LaunchDataDescriptor
 */
public class LaunchDataDescriptorTest extends DateDescriptorTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_countNegative() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), -1, 1,
        Collections.<String> emptySet());
  }

  @Test
  public void testConstructor_countZero() {
    try {
      createDescriptor(new LocalDate(), createLaunchConfiguration(), 0, 1,
          Collections.<String> emptySet());
    } catch (IllegalArgumentException e) {
      fail("0 should be accepted");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_durationNegative() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), 1, -1,
        Collections.<String> emptySet());
  }

  @Test
  public void testConstructor_durationZero() {
    try {
      createDescriptor(new LocalDate(), createLaunchConfiguration(), 1, 0,
          Collections.<String> emptySet());
    } catch (IllegalArgumentException e) {
      fail("0 should be accepted");
    }
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_fileIdsNull() {
    createDescriptor(new LocalDate(), createLaunchConfiguration(), 1, 2, null);
  }

  @Override
  public void testHashCode() {
    int count = 12;
    long value = 19083;
    LocalDate date = new LocalDate();
    List<String> fileIds = Arrays.asList("1", "3");
    LaunchConfigurationDescriptor config = createLaunchConfiguration();
    int hashCode = config.hashCode();

    LaunchDataDescriptor des = createDescriptor(date, config, count, value,
        fileIds);
    assertEquals(hashCode, des.hashCode());
  }

  @Override
  public void testEquals() {
    int count = 12;
    long value = 19083;
    LocalDate date = new LocalDate();
    List<String> fileIds = new ArrayList<String>(Arrays.asList("1", "3"));
    LaunchConfigurationDescriptor config = createLaunchConfiguration();

    LaunchDataDescriptor des1 = createDescriptor(date, config, count, value,
        fileIds);
    assertTrue(des1.equals(des1));
    assertFalse(des1.equals(null));
    assertFalse(des1.equals(""));

    LaunchDataDescriptor des2 = createDescriptor(date, config, count, value,
        fileIds);
    assertTrue(des1.equals(des2));
    assertTrue(des2.equals(des1));

    des2 = createDescriptor(date.plusDays(1), config, count, value, fileIds);
    assertFalse(des1.equals(des2));

    des2 = createDescriptor(date, new LaunchConfigurationDescriptor(System
        .currentTimeMillis()
        + "", "", ""), count, value, fileIds);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, config, count + 1, value, fileIds);
    assertFalse(des1.equals(des2));
    
    des2 = createDescriptor(date, config, count, value + 1, fileIds);
    assertFalse(des1.equals(des2));
    
    fileIds.add(System.nanoTime() + "");
    des2 = createDescriptor(date, config, count, value, fileIds);
    assertFalse(des1.equals(des2));
  }

  @Test(expected = NullPointerException.class)
  public void testCosntructor_launchConfigNull() {
    createDescriptor(new LocalDate(), null, 2, 2, Collections
        .<String> emptySet());
  }

  @Test
  public void testGetFileIds() {
    ImmutableSet<String> fileIds = ImmutableSet.of("a", "b");
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), 1, 3, fileIds);
    assertEquals(fileIds, des.getFileIds());
  }

  @Test
  public void testGetLaunchConfigurationDescriptor() {
    LaunchConfigurationDescriptor config = createLaunchConfiguration();
    LaunchDataDescriptor des = createDescriptor(new LocalDate(), config, 1, 1,
        Collections.<String> emptySet());
    assertEquals(config, des.getLaunchDescriptor());
  }

  @Test
  public void testGetLaunchCount() {
    int count = 2387;
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), count, 1, Collections.<String> emptySet());
    assertEquals(count, des.getLaunchCount());
  }

  @Test
  public void testGetTotalDuration() {
    long duration = 98238734;
    LaunchDataDescriptor des = createDescriptor(new LocalDate(),
        createLaunchConfiguration(), 1, duration, Collections
            .<String> emptySet());
    assertEquals(duration, des.getTotalDuration());
  }

  @Override
  protected final LaunchDataDescriptor createDescriptor(LocalDate date) {
    return createDescriptor(date, createLaunchConfiguration(), 1, 2,
        Collections.<String> emptySet());
  }

  /**
   * Creates a descriptor for testing.
   * 
   * @param date The date.
   * @param des The launch configuration descriptor.
   * @param count The launch count.
   * @param duration The launch duration.
   * @param fileIds The file IDs.
   * @return A descriptor created using the parameters.
   */
  protected LaunchDataDescriptor createDescriptor(LocalDate date,
      LaunchConfigurationDescriptor des, int count, long duration,
      Iterable<String> fileIds) {

    return new LaunchDataDescriptor(date, des, count, duration, fileIds);
  }

  private LaunchConfigurationDescriptor createLaunchConfiguration() {
    return new LaunchConfigurationDescriptor("1", "2", "3");
  }

}