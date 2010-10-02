package rabbit.data.test.access.model;

import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskFileDataDescriptor
 */
@SuppressWarnings({"deprecation", "restriction"})
public class TaskFileDataDescriptorTest extends FileDataDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_taskIdNull() {
    createDescriptor(new LocalDate(), new Duration(1), new Path("/p/p/abc"),
        null);
  }

  @Test
  public void testFindTask() {
    String taskId = "100";
    LocalTask task = new LocalTask(taskId, "summary");
    task.setCreationDate(new Date(1999, 1, 1));

    // Add test to task list:
    TasksUiPlugin.getTaskList().addTask(task,
        TasksUiPlugin.getTaskList().getCategories().iterator().next());
    assertNotNull(TasksUi.getRepositoryModel().getTask(
        task.getHandleIdentifier()));

    // Task with same ID and creation date:
    TaskFileDataDescriptor des = createDescriptor(new LocalDate(),
        new Duration(1), new Path("/p/a.b"), new TaskId(
            task.getHandleIdentifier(), task.getCreationDate()));
    assertNotNull(des.findTask());

    // Task with same ID but different creation date:
    des = createDescriptor(new LocalDate(), new Duration(1),
        new Path("/p/a.b"), new TaskId(task.getHandleIdentifier(), new Date(
            1000, 1, 1)));
    assertNull(des.findTask());
  }

  @Test
  public void testGetTaskId() {
    TaskId id = new TaskId("helloWorld", new Date(1999, 1, 1));
    assertEquals(id, createDescriptor(new LocalDate(), new Duration(1),
        new Path("/p"), id).getTaskId());
  }

  @Override
  protected final TaskFileDataDescriptor createDescriptor(LocalDate date,
      Duration duration, IPath filePath) {
    return createDescriptor(date, duration, filePath, new TaskId("a",
        new Date()));
  }

  /**
   * @see TaskFileDataDescriptor#TaskFileDataDescriptor(LocalDate, long, IPath,
   *      TaskId)
   */
  protected TaskFileDataDescriptor createDescriptor(LocalDate date,
      Duration value, IPath filePath, TaskId taskId) {
    return new TaskFileDataDescriptor(date, value, filePath, taskId);
  }
}
