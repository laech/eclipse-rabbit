package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import rabbit.ui.ColumnComparator;

/**
 * Test for {@link AbstractGraphTreePage}
 */
public abstract class AbstractGraphTreePageTest {

	protected AbstractGraphTreePage page;

	protected abstract AbstractGraphTreePage createPage();

	@Before
	public void setUp() {
		page = createPage();
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				page.createContents(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			}
		});
	}

	@Test
	public void testGetSetMaxValue() {
		page.setMaxValue(10);
		assertEquals(10, page.getMaxValue());
		page.setMaxValue(101);
		assertEquals(101, page.getMaxValue());
	}

	@Test
	public void testGetGraphColumn() throws Exception {
		assertNotNull(page.getGraphColumn());
	}

	@Test
	public void testGetValueColumn() throws Exception {
		assertNotNull(page.getValueColumn());
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
	public void testCreateColumns() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Tree tree = new Tree(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
					assertNotNull(createColumns(page, tree));
				} catch (Exception e) {
					fail();
				}
			}
		});
	}

	@Test
	public void testGetImage() {
		assertNotNull(page.getImage());
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

	protected void saveState(AbstractGraphTreePage page) throws Exception {
		Method saveState = AbstractGraphTreePage.class.getDeclaredMethod("saveState");
		saveState.setAccessible(true);
		saveState.invoke(page);
	}

	protected void restoreState(AbstractGraphTreePage page) throws Exception {
		Method restoreState = AbstractGraphTreePage.class.getDeclaredMethod("restoreState");
		restoreState.setAccessible(true);
		restoreState.invoke(page);
	}

	protected TreeColumn[] createColumns(AbstractGraphTreePage page, Tree tree) throws Exception {
		Method createColumns = AbstractGraphTreePage.class.getDeclaredMethod("createColumns", Tree.class);
		createColumns.setAccessible(true);
		return (TreeColumn[]) createColumns.invoke(page, tree);
	}

	protected ColumnComparator createComparator(AbstractGraphTreePage page, TreeViewer viewer) throws Exception {
		Method createComparator = AbstractGraphTreePage.class.getDeclaredMethod("createComparator", TreeViewer.class);
		createComparator.setAccessible(true);
		return (ColumnComparator) createComparator.invoke(page, viewer);
	}

	protected IStructuredContentProvider createContentProvider(AbstractGraphTreePage page) throws Exception {
		Method createContentProvider = AbstractGraphTreePage.class.getDeclaredMethod("createContentProvider");
		createContentProvider.setAccessible(true);
		return (IStructuredContentProvider) createContentProvider.invoke(page);
	}

	protected ITableLabelProvider createLabelProvider(AbstractGraphTreePage page) throws Exception {
		Method createLabelProvider = AbstractGraphTreePage.class.getDeclaredMethod("createLabelProvider");
		createLabelProvider.setAccessible(true);
		return (ITableLabelProvider) createLabelProvider.invoke(page);
	}
}
