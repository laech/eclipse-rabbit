package rabbit.data.test.access.model;

import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskFileDataDescriptor
 */
public class TaskFileDataDescriptorTest extends FileDataDescriptorTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_taskIdNull() {
    createDescriptor(new LocalDate(), 1, "ab", null);
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    String fileId = "hello";
    TaskId id = new TaskId("world", new Date(2000, 1, 1));
    assertEquals(Objects.hashCode(date, fileId, id),
        createDescriptor(date, 0, fileId, id).hashCode());
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void testEquals() {
    TaskFileDataDescriptor d1 = createDescriptor(
        new LocalDate(1998, 1, 1), 10, "a", new TaskId("b", new Date()));
    TaskFileDataDescriptor d2 = createDescriptor(
        d1.getDate(), d1.getValue(), d1.getFileId(), d1.getTaskId());
    
    assertTrue(d1.equals(d2));
    assertTrue(d1.equals(d1));
    assertFalse(d1.equals(null));

    d2 = createDescriptor(d1.getDate().minusDays(1), d1.getValue(), 
        d1.getFileId(), d1.getTaskId());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue() + 1, d1.getFileId(), 
        d1.getTaskId());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFileId() + "1", 
        d1.getTaskId());
    assertFalse(d1.equals(d2));

    TaskId id2 = new TaskId(d1.getTaskId().getHandleIdentifier(), 
        new Date(3000, 1, 1));
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFileId(), id2);
    assertFalse(d1.equals(d2));
    
    id2 = new TaskId(d1.getTaskId().getHandleIdentifier() + "1", 
        d1.getTaskId().getCreationDate());
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFileId(), id2);
    assertFalse(d1.equals(d2));
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testGetTaskId() {
    TaskId id = new TaskId("helloWorld", new Date(1999, 1, 1));
    assertEquals(id, createDescriptor(new LocalDate(), 1, "1", id).getTaskId());
  }

  @Override
  protected final TaskFileDataDescriptor createDescriptor(LocalDate date, 
      long duration, String fileId) {
    return createDescriptor(date, duration, fileId, new TaskId("a", new Date()));
  }
  
  /**
   * @see TaskFileDataDescriptor#TaskFileDataDescriptor(LocalDate, long, String, TaskId)
   */
  protected TaskFileDataDescriptor createDescriptor(LocalDate date, long value,
      String fileId, TaskId taskId) {
    return new TaskFileDataDescriptor(date, value, fileId, taskId);
  }
}
