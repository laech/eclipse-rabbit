package rabbit.ui.tests.pages;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.pages.PerspectivePage;
import rabbit.ui.internal.pages.PerspectivePageContentProvider;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see PerspectivePageContentProvider
 */
@SuppressWarnings("restriction")
public class PerspectivePageContentProviderTest {

  private static PerspectivePageContentProvider contents;
  private static PerspectivePage page;
  private static Shell shell;

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new PerspectivePage();
    page.createContents(shell);

    contents = new PerspectivePageContentProvider(page);
    page.getViewer().setContentProvider(contents);
  }

  @Test
  public void testGetChildren() {
    PerspectiveDataDescriptor des1 = new PerspectiveDataDescriptor(
        new LocalDate(), 8723, "123");
    PerspectiveDataDescriptor des2 = new PerspectiveDataDescriptor(des1
        .getDate(), 18723, "12343");

    page.getViewer().setInput(Arrays.asList(des1, des2));
    Set<PerspectiveDataDescriptor> set = Sets.newHashSet(des1, des2);

    Object[] elements = contents.getChildren(des1.getDate());
    assertEquals(2, elements.length);
    assertTrue(set.containsAll(Arrays.asList(elements)));
  }

  @Test
  public void testGetPerspective_nonExistId() {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(
        new LocalDate(), 8723, System.currentTimeMillis() + "");
    IPerspectiveDescriptor perspective = contents.getPerspective(des);
    // A custom perspective descriptor is returned, never null:
    assertNotNull(perspective);
    assertEquals(des.getPerspectiveId(), perspective.getId());
  }

  @Test
  public void testGetValueOfPerspective_oneElement() {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(
        new LocalDate(), 8723, "24");
    page.getViewer().setInput(Arrays.asList(des));
    assertEquals(des.getValue(), contents.getValueOfPerspective(contents
        .getPerspective(des)));
  }

  @Test
  public void testGetValueOfPerspective_twoElements() {
    PerspectiveDataDescriptor des1 = new PerspectiveDataDescriptor(
        new LocalDate(), 8723, "24");
    PerspectiveDataDescriptor des2 = new PerspectiveDataDescriptor(des1
        .getDate().plusDays(1), 83276723, des1.getPerspectiveId());
    page.getViewer().setInput(Arrays.asList(des1, des2));
    assertEquals(des1.getValue() + des2.getValue(), contents
        .getValueOfPerspective(contents.getPerspective(des1)));
  }

  @Test
  public void testHasChildren() {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(
        new LocalDate(), 187, "a.b");
    page.getViewer().setInput(Arrays.asList(des));
    assertTrue(contents.hasChildren(des.getDate()));
    assertFalse(contents.hasChildren(des));
    assertFalse(contents.hasChildren(des.getDate().plusDays(1)));
  }

  @Test
  public void testInputChanged_newInputNull_clearsExistingData() {
    PerspectiveDataDescriptor des1 = new PerspectiveDataDescriptor(
        new LocalDate(), 8723, "123");
    Set<PerspectiveDataDescriptor> set = Sets.newHashSet(des1);
    page.getViewer().setInput(set);
    assertTrue(contents.hasChildren(des1.getDate()));

    contents.inputChanged(page.getViewer(), set, null);
    assertFalse(contents.hasChildren(des1.getDate()));
  }

  @Test
  public void testInputChanged_newInputNull_noException() {
    try {
      contents.inputChanged(page.getViewer(), null, null);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testIsDisplayingByDate_defaultTrue() {
    assertTrue(contents.isDisplayingByDate());
  }

  @Test
  public void testSetDisplayByDate() {
    contents.setDisplayByDate(false);
    assertFalse(contents.isDisplayingByDate());
    contents.setDisplayByDate(true);
    assertTrue(contents.isDisplayingByDate());
  }
}
