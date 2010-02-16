package rabbit.ui.internal.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link AbstractGraphTreePage}
 */
public abstract class AbstractGraphTablePageTest {

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
		assertTrue(Double.compare(10, page.getMaxValue()) == 0);
		page.setMaxValue(101);
		assertTrue(Double.compare(101, page.getMaxValue()) == 0);
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
		assertNotNull(page.createContentProvider());
	}

	@Test
	public void testCreateLabelProvider() throws Exception {
		assertNotNull(page.createLabelProvider());
	}

	@Test
	public void testCreateColumns() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Tree tree = new Tree(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.NONE);
					assertNotNull(page.createColumns(tree));
				} catch (Exception e) {
					Assert.fail();
				}
			}
		});
	}
}
