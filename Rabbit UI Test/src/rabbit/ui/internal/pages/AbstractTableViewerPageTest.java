package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

/**
 * @see AbstractTableViewerPage
 */
public abstract class AbstractTableViewerPageTest extends AbstractValueProviderPageTest {

	protected AbstractTableViewerPage page;

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
	public void testCreateLabelProvider() throws Exception {
		assertNotNull(createLabelProvider(page));
	}

	@Test
	public void testSaveState() throws Exception {
		int width = 12;
		for (TableColumn column : page.getViewer().getTable().getColumns()) {
			column.setWidth(width);
		}
		saveState(page);

		for (TableColumn column : page.getViewer().getTable().getColumns()) {
			column.setWidth(width * 2);
		}

		restoreState(page);
		for (TableColumn column : page.getViewer().getTable().getColumns()) {
			assertEquals(width, column.getWidth());
		}
	}

	protected void saveState(AbstractTableViewerPage page) throws Exception {
		Method saveState = AbstractTableViewerPage.class.getDeclaredMethod("saveState");
		saveState.setAccessible(true);
		saveState.invoke(page);
	}

	protected void restoreState(AbstractTableViewerPage page) throws Exception {
		Method restoreState = AbstractTableViewerPage.class.getDeclaredMethod("restoreState");
		restoreState.setAccessible(true);
		restoreState.invoke(page);
	}

	protected IContentProvider createContentProvider(AbstractTableViewerPage page) throws Exception {
		Method createContentProvider = AbstractTableViewerPage.class.getDeclaredMethod("createContentProvider");
		createContentProvider.setAccessible(true);
		return (IContentProvider) createContentProvider.invoke(page);
	}

	protected ITableLabelProvider createLabelProvider(AbstractTableViewerPage page) throws Exception {
		Method createLabelProvider = AbstractTableViewerPage.class.getDeclaredMethod("createLabelProvider");
		createLabelProvider.setAccessible(true);
		return (ITableLabelProvider) createLabelProvider.invoke(page);
	}

	@Override
	protected abstract AbstractTableViewerPage createPage();

}
