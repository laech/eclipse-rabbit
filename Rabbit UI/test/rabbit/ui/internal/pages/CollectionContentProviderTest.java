package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link CollectionContentProvider}
 */
public class CollectionContentProviderTest {

	private CollectionContentProvider provider = new CollectionContentProvider();
	private Collection<Object> emptyCollection = Collections.emptyList();
	private Collection<Object> notEmptyCollection = new ArrayList<Object>();

	@Before
	public void setUp() {
		notEmptyCollection.clear();
		notEmptyCollection.add(new Object());
		notEmptyCollection.add(new String());
	}

	@Test
	public void testGetElements() {
		assertNotNull(provider.getElements(emptyCollection));
		assertEquals(emptyCollection.size(), provider.getElements(emptyCollection).length);

		assertNotNull(provider.getElements(notEmptyCollection));
		assertEquals(notEmptyCollection.size(), provider.getElements(notEmptyCollection).length);
	}

	@Test
	public void testGetChildren() {
		assertNotNull(provider.getChildren(emptyCollection));
		assertEquals(emptyCollection.size(), provider.getChildren(emptyCollection).length);

		assertNotNull(provider.getChildren(notEmptyCollection));
		assertEquals(notEmptyCollection.size(), provider.getChildren(notEmptyCollection).length);
	}

	@Test
	public void testHasChildren() {
		assertTrue(provider.hasChildren(notEmptyCollection));
	}

}
