package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class AbstractValueProviderPageTest {

	protected abstract AbstractValueProviderPage createPage();

	@Test
	public void testGetSetMaxValue() {
		AbstractValueProviderPage page = createPage();
		page.setMaxValue(10);
		assertEquals(10, page.getMaxValue());
		page.setMaxValue(101);
		assertEquals(101, page.getMaxValue());
	}
}
