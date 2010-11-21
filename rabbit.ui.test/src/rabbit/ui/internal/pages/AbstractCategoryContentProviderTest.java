package rabbit.ui.internal.pages;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @see AbstractCategoryContentProvider
 */
public abstract class AbstractCategoryContentProviderTest {

  protected static final Shell SHELL = 
      new Shell(PlatformUI.getWorkbench().getDisplay());
  
  @AfterClass
  public static void afterClass() {
    SHELL.dispose();
  }
  
  protected TreeViewer viewer;
  protected AbstractCategoryContentProvider provider;
  
  @Before
  public void before() {
    viewer = new TreeViewer(SHELL);
    provider = create(viewer);
  }
  
  @After
  public void after() {
    viewer.getTree().dispose();
  }
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_treeViewerNull() {
    create(null);
  }
  
  @Test
  public void testConstructor_treeViewerNotNull() {
    create(new TreeViewer(SHELL)); // No exception
  }
  
  @Test
  public void testGetCategorizers_empty() {
    assertThat(provider.getCategorizers(), notNullValue());
    assertThat(provider.getCategorizers().size(), is(0));
  }
  
  @Test
  public void testGetChildren_argNullReturnsNull() {
    assertThat(provider.getChildren(null), nullValue());
  }
  
  @Test
  public void testGetChildren_argNotTreeNodeReturnsNull() {
    assertThat(provider.getChildren("abc"), nullValue());
  }
  
  @Test
  public void testGetChildren_argTreeNode() {
    TreeNode node = new TreeNode("");
    node.setChildren(new TreeNode[] { new TreeNode(1), new TreeNode(2) });
    assertThat(provider.getChildren(node), is((Object[]) node.getChildren()));
  }
  
  @Test
  public void testGetElements_argNull() {
    assertThat(provider.getElements(null), is(nullValue()));
  }
  
  @Test
  public void testGetElements() {
    // The implementation of getElements(Object) 
  }
  
  protected abstract AbstractCategoryContentProvider create(TreeViewer viewer);
}
