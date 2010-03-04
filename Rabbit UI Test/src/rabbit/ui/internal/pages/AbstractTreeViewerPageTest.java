package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import rabbit.ui.TreeLabelComparator;

/**
 * @see AbstractTreeViewerPage
 */
public abstract class AbstractTreeViewerPageTest extends AbstractValueProviderPageTest {

	private AbstractTreeViewerPage page;

	@Before
	public void setUp() {
		page = createPage();
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				page.createContents(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell());
			}
		});
	}

	@Test
	public void testGetViewer() throws Exception {
		assertNotNull(page.getViewer());
	}

	@Test
	public void testCreateContentProvider() throws Exception {
		assertNotNull(createContentProvider(page));
	}

	@Test
	public void testCreateComparator() throws Exception {
		assertNotNull(createComparator(page, page.getViewer()));
	}

	@Test
	public void testCreateLabelProvider() throws Exception {
		assertNotNull(createLabelProvider(page));
	}

	@Test
	public void testSaveState() throws Exception {
		int width = 12;
		for (TreeColumn column : page.getViewer().getTree().getColumns()) {
			column.setWidth(width);
		}
		saveState(page);

		for (TreeColumn column : page.getViewer().getTree().getColumns()) {
			column.setWidth(width * 2);
		}

		restoreState(page);
		for (TreeColumn column : page.getViewer().getTree().getColumns()) {
			assertEquals(width, column.getWidth());
		}
	}

	protected void saveState(AbstractTreeViewerPage page) throws Exception {
		Method saveState = AbstractTreeViewerPage.class.getDeclaredMethod("saveState");
		saveState.setAccessible(true);
		saveState.invoke(page);
	}

	protected void restoreState(AbstractTreeViewerPage page) throws Exception {
		Method restoreState = AbstractTreeViewerPage.class.getDeclaredMethod("restoreState");
		restoreState.setAccessible(true);
		restoreState.invoke(page);
	}

	protected TreeLabelComparator createComparator(AbstractTreeViewerPage page, TreeViewer viewer) throws Exception {
		Method createComparator = AbstractTreeViewerPage.class.getDeclaredMethod("createComparator", TreeViewer.class);
		createComparator.setAccessible(true);
		return (TreeLabelComparator) createComparator.invoke(page, viewer);
	}

	protected IContentProvider createContentProvider(AbstractTreeViewerPage page) throws Exception {
		Method createContentProvider = AbstractTreeViewerPage.class.getDeclaredMethod("createContentProvider");
		createContentProvider.setAccessible(true);
		return (IContentProvider) createContentProvider.invoke(page);
	}

	protected ITableLabelProvider createLabelProvider(AbstractTreeViewerPage page) throws Exception {
		Method createLabelProvider = AbstractTreeViewerPage.class.getDeclaredMethod("createLabelProvider");
		createLabelProvider.setAccessible(true);
		return (ITableLabelProvider) createLabelProvider.invoke(page);
	}
	
	@Override
	protected abstract AbstractTreeViewerPage createPage();
}
