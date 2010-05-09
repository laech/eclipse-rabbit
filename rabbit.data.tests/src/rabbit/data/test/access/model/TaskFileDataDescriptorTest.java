package rabbit.data.test.access.model;

import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskFileDataDescriptor
 */
@SuppressWarnings( { "deprecation", "restriction" } )
public class TaskFileDataDescriptorTest extends FileDataDescriptorTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_taskIdNull() {
    createDescriptor(new LocalDate(), 1, new Path("/p/p/abc"), null);
  }
  
  @Override
  public void testHashCode() {
    LocalDate date = new LocalDate();
    IPath filePath = new Path("/p/p/a");
    TaskId id = new TaskId("world", new Date(2000, 1, 1));
    assertEquals(Objects.hashCode(date, filePath, id),
        createDescriptor(date, 0, filePath, id).hashCode());
  }
  
  @Override
  public void testEquals() {
    TaskFileDataDescriptor d1 = createDescriptor(
        new LocalDate(1998, 1, 1), 10, new Path("/p/f/a.txt"), new TaskId("b", new Date()));
    TaskFileDataDescriptor d2 = createDescriptor(
        d1.getDate(), d1.getValue(), d1.getFilePath(), d1.getTaskId());
    
    assertTrue(d1.equals(d2));
    assertTrue(d1.equals(d1));
    assertFalse(d1.equals(null));

    d2 = createDescriptor(d1.getDate().minusDays(1), d1.getValue(), 
        d1.getFilePath(), d1.getTaskId());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue() + 1, d1.getFilePath(), 
        d1.getTaskId());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFilePath().append("1"), 
        d1.getTaskId());
    assertFalse(d1.equals(d2));

    TaskId id2 = new TaskId(d1.getTaskId().getHandleIdentifier(), 
        new Date(3000, 1, 1));
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFilePath(), id2);
    assertFalse(d1.equals(d2));
    
    id2 = new TaskId(d1.getTaskId().getHandleIdentifier() + "1", 
        d1.getTaskId().getCreationDate());
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getFilePath(), id2);
    assertFalse(d1.equals(d2));
  }
  
  @Test
  public void testFindTask() {
    String taskId = "100";
    LocalTask task = new LocalTask(taskId, "summary");
    task.setCreationDate(new Date(1999, 1, 1));
    
    // Add test to task list:
    TasksUiPlugin.getTaskList().addTask(task, 
        TasksUiPlugin.getTaskList().getCategories().iterator().next());
    assertNotNull(TasksUi.getRepositoryModel().getTask(task.getHandleIdentifier()));
    
    // Task with same ID and creation date:
    TaskFileDataDescriptor des = createDescriptor(new LocalDate(), 1, 
        new Path("/p/a.b"), new TaskId(task.getHandleIdentifier(), task.getCreationDate()));
    assertNotNull(des.findTask());
    
    // Task with same ID but different creation date:
    des = createDescriptor(new LocalDate(), 1, 
        new Path("/p/a.b"), new TaskId(task.getHandleIdentifier(), new Date(1000, 1, 1)));
    assertNull(des.findTask());
  }
  
  @Test
  public void testGetTaskId() {
    TaskId id = new TaskId("helloWorld", new Date(1999, 1, 1));
    assertEquals(id, createDescriptor(new LocalDate(), 1, new Path("/p"), id).getTaskId());
  }

  @Override
  protected final TaskFileDataDescriptor createDescriptor(LocalDate date, 
      long duration, IPath filePath) {
    return createDescriptor(date, duration, filePath, new TaskId("a", new Date()));
  }
  
  /**
   * @see TaskFileDataDescriptor#TaskFileDataDescriptor(LocalDate, long, IPath, TaskId)
   */
  protected TaskFileDataDescriptor createDescriptor(LocalDate date, long value,
      IPath filePath, TaskId taskId) {
    return new TaskFileDataDescriptor(date, value, filePath, taskId);
  }
}
