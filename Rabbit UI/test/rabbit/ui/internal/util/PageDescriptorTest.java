package rabbit.ui.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
	private ImageDescriptor image = PlatformUI.getWorkbench()
			.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD);

	private IPage page = new TestPage();

	@Before
	public void setUp() {
		pageDescriptor = new PageDescriptor(name, page, description, image);
	}

	@Test
	public void testAddChild() {
		PageDescriptor p2 = new PageDescriptor(name, page, description, image);
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
			new PageDescriptor(name, page, null, image);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testConstructor_withImageNull() {
		try {
			new PageDescriptor(name, page, description, null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withNameNull() {
		new PageDescriptor(null, page, description, image);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_withPageNull() {
		new PageDescriptor(name, null, description, image);
	}

	@Test
	public void testGetChildren() {
		assertNotNull(pageDescriptor.getChildren());
		assertEquals(0, pageDescriptor.getChildren().size());
		PageDescriptor p2 = new PageDescriptor(name, page, description, image);
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
	public void testGetImage() {
		assertSame(image, pageDescriptor.getImageDescriptor());
	}

	@Test
	public void testRemoveChild() {
		assertFalse(pageDescriptor.removeChild(pageDescriptor));
		PageDescriptor p2 = new PageDescriptor(name, page, description, image);
		assertTrue(pageDescriptor.addChild(p2));
		assertTrue(pageDescriptor.removeChild(p2));
		assertFalse(pageDescriptor.removeChild(p2));
	}

}
