package rabbit.ui.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;

import rabbit.ui.DisplayPreference;
import rabbit.ui.IPage;

/**
 * Test for {@link PageDescriptor}
 */
public class PageDescriptorTest {

	/** Helper class for testing. */
	private static class TestPage implements IPage {

		@Override
		public void createContents(Composite parent) {
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public void update(DisplayPreference preference) {
		}

		@Override
		public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
			return null;
		}

	}

	private PageDescriptor pageDescriptor;
	private String description = "dEsCrIpTiOn";
	private String name = "NaMe";

	private IPage page = new TestPage();

	@Before
	public void setUp() {
		pageDescriptor = new PageDescriptor(name, page, description);
	}

	@Test
	public void testAddChild() {
		PageDescriptor p2 = new PageDescriptor(name, page, description);
		assertTrue(pageDescriptor.addChild(p2));
		assertEquals(1, pageDescriptor.getChildren().size());
		assertFalse(pageDescriptor.addChild(p2));
		assertEquals(1, pageDescriptor.getChildren().size());
	}

	@Test
	public void testAddChild_self() {
		assertFalse(pageDescriptor.addChild(pageDescriptor));
	}

	@Test
	public void testConstructor_withDescriptionNull() {
		try {
			new PageDescriptor(name, page, null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withNameNull() {
		new PageDescriptor(null, page, description);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withPageNull() {
		new PageDescriptor(name, null, description);
	}

	@Test
	public void testGetChildren() {
		assertNotNull(pageDescriptor.getChildren());
		assertEquals(0, pageDescriptor.getChildren().size());
		PageDescriptor p2 = new PageDescriptor(name, page, description);
		assertTrue(pageDescriptor.addChild(p2));
		assertEquals(1, pageDescriptor.getChildren().size());
	}

	@Test
	public void testGetDescription() {
		assertEquals(description, pageDescriptor.getDescription());
	}

	@Test
	public void testGetName() {
		assertEquals(name, pageDescriptor.getName());
	}

	@Test
	public void testGetPage() {
		assertSame(page, pageDescriptor.getPage());
	}

	@Test
	public void testRemoveChild() {
		assertFalse(pageDescriptor.removeChild(pageDescriptor));
		PageDescriptor p2 = new PageDescriptor(name, page, description);
		assertTrue(pageDescriptor.addChild(p2));
		assertTrue(pageDescriptor.removeChild(p2));
		assertFalse(pageDescriptor.removeChild(p2));
	}

}
