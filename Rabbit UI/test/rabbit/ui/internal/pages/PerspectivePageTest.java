package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Map;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.PerspectiveDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

/**
 * Test for {@link PerspectivePage}
 */
public class PerspectivePageTest extends AbstractGraphTreePageTest {
	@Override
	protected AbstractGraphTreePage createPage() {
		return new PerspectivePage();
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor accessor = new PerspectiveDataAccessor();

		DisplayPreference pref = new DisplayPreference();
		Map<String, Long> data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());

		pref.getStartDate().add(Calendar.MONTH, -1);
		pref.getEndDate().add(Calendar.DAY_OF_MONTH, -5);
		data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		max = 0;
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());
	}

	@Test
	public void testGetValue() throws Exception {
		long value = 9823;
		IPerspectiveDescriptor perspective = new UndefinedPerspectiveDescriptor("abc");
		Map<IPerspectiveDescriptor, Long> data = getData((PerspectivePage) page);
		data.put(perspective, value);

		assertEquals(value, page.getValue(perspective));
		assertEquals(0, page.getValue(new Object()));
	}

	@SuppressWarnings("unchecked")
	static Map<IPerspectiveDescriptor, Long> getData(PerspectivePage page) throws Exception {
		Field field = PerspectivePage.class.getDeclaredField("dataMapping");
		field.setAccessible(true);
		return (Map<IPerspectiveDescriptor, Long>) field.get(page);
	}
}
