package rabbit.ui.internal.pages;

import org.eclipse.ui.PlatformUI;
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
	public void testGetGraphColumn() {
		assertNotNull(page.getGraphColumn());
	}

	@Test
	public void testGetValueColumn() {
		assertNotNull(page.getValueColumn());
	}

	@Test
	public void testGetViewer() {
		assertNotNull(page.getViewer());
	}

	@Test
	public void testCreateContentProvider() {
		assertNotNull(page.createContentProvider());
	}

	@Test
	public void testCreateLabelProvider() {
		assertNotNull(page.createLabelProvider());
	}

	@Test
	public void testCreateColumns() {
		assertNotNull(page.createColumns(page.getViewer().getTree()));
	}
}
