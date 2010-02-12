package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.TestUtil;

/**
 * Test {@link XmlResourceManager}
 */
public class XmlResourceManagerTest {

	private XmlResourceManager mapper = XmlResourceManager.INSTANCE;

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtil.setUpPathForTesting();
	}

	@Before
	public void setUp() {
		getDataFile().delete();
	}

	private File getDataFile() {
		try {
			Method dataFile = XmlResourceManager.class.getDeclaredMethod("getDataFile");
			dataFile.setAccessible(true);
			File file = (File) dataFile.invoke(mapper);
			return file;
		} catch (Exception e) {
			Assert.fail();
			return null;
		}
	}

	@Test
	public void testGetFilePath() {
		String id = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(mapper.getFilePath(id));
	}

	@Test
	public void testGetId() {
		String path = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(mapper.getId(path));
	}

	@Test
	public void testInsert() {
		String path = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(mapper.getId(path));
		assertNotNull(mapper.insert(path));
		assertNotNull(mapper.getId(path));
	}

	@Test
	public void testPostShutdown() {
		assertFalse(getDataFile().exists());
		mapper.postShutdown(PlatformUI.getWorkbench());
		assertTrue(getDataFile().exists());
	}
}
