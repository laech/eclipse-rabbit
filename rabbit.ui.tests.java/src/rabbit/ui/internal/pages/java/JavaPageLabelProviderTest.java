package rabbit.ui.internal.pages.java;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.ui.internal.pages.java.JavaCategory;
import rabbit.ui.internal.pages.java.JavaPageContentProvider;
import rabbit.ui.internal.pages.java.JavaPageLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see JavaPageLabelProvider
 */
public class JavaPageLabelProviderTest {

  /** A java element, doesn't exist in workspace. */
  private static final IType element;
  private static final LocalDate date;

  private static final Shell shell;
  private static final JavaPageLabelProvider labelProvider;
  private static final JavaPageContentProvider contentProvider;

  static {
    element = (IType) JavaCore.create("=Proj/src<enfo{Something.java[Something");
    date = new LocalDate();

    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    shell.setLayout(new FillLayout());
    TreeViewer viewer = new TreeViewer(shell);
    contentProvider = new JavaPageContentProvider(viewer);
    labelProvider = new JavaPageLabelProvider(contentProvider);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Test
  public void testGetText() throws Exception {
    assertNotNull(labelProvider.getText(element));
    assertNotNull(labelProvider.getText(date));
    assertNotNull(labelProvider.getText(new TreeNode(element)));
    assertNotNull(labelProvider.getText(new TreeNode(date)));
  }

  @Test
  public void testGetImage() throws Exception {
    assertNotNull(labelProvider.getImage(element));
    assertNotNull(labelProvider.getImage(date));
    assertNotNull(labelProvider.getImage(new TreeNode(element)));
    assertNotNull(labelProvider.getImage(new TreeNode(date)));
  }

  @Test
  public void testGetForeground() throws Exception {
    IJavaModel java = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
    assertNull(labelProvider.getForeground(java));
    assertNull(labelProvider.getForeground(new TreeNode(java)));
    assertNull(labelProvider.getForeground(date));
    assertNull(labelProvider.getForeground(new TreeNode(date)));

    assertFalse(element.exists());
    assertNotNull(labelProvider.getForeground(element));
    assertNotNull(labelProvider.getForeground(new TreeNode(element)));
  }

  @Test
  public void testGetBackground() throws Exception {
    IJavaModel java = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
    assertNull(labelProvider.getBackground(java));
    assertNull(labelProvider.getBackground(new TreeNode(java)));
    assertNull(labelProvider.getBackground(date));
    assertNull(labelProvider.getBackground(new TreeNode(date)));
    assertNull(labelProvider.getBackground(element));
    assertNull(labelProvider.getBackground(new TreeNode(element)));
  }

  @Test
  public void testGetColumnText_0() throws Exception {
    assertEquals(labelProvider.getText(element), labelProvider.getColumnText(
        element, 0));
    assertEquals(labelProvider.getText(new TreeNode(element)),
        labelProvider.getColumnText(new TreeNode(element), 0));
    assertEquals(labelProvider.getText(date), labelProvider.getColumnText(date,
        0));
    assertEquals(labelProvider.getText(new TreeNode(date)),
        labelProvider.getColumnText(new TreeNode(date), 0));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    JavaDataDescriptor des = new JavaDataDescriptor(new LocalDate(),
        new Duration(12234), element.getHandleIdentifier());

    contentProvider.getViewer().setInput(Arrays.asList(des));

    contentProvider.setSelectedCategories(JavaCategory.MEMBER);
    contentProvider.setPaintCategory(JavaCategory.MEMBER);
    TreeNode node = contentProvider.getRoot().getChildren()[0];
    assertEquals(des.getDuration().getMillis(), contentProvider.getValue(node));
    assertEquals(format(contentProvider.getValue(node)),
        labelProvider.getColumnText(node, 1));

    contentProvider.setSelectedCategories(JavaCategory.DATE);
    contentProvider.setPaintCategory(JavaCategory.DATE);
    node = contentProvider.getRoot().getChildren()[0];
    assertEquals(des.getDuration().getMillis(), contentProvider.getValue(node));
    assertEquals(format(contentProvider.getValue(node)),
        labelProvider.getColumnText(node, 1));
  }

  @Test
  public void testGetColumnImage_0() throws Exception {
    assertNotNull(labelProvider.getColumnImage(element, 0));
    assertNotNull(labelProvider.getColumnImage(date, 0));
    assertNotNull(labelProvider.getColumnImage(new TreeNode(element), 0));
    assertNotNull(labelProvider.getColumnImage(new TreeNode(date), 0));
  }
}
