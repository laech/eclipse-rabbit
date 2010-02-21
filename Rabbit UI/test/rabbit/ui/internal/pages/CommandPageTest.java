package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.CommandDataAccessor;
import rabbit.ui.DisplayPreference;

/**
 * Test for {@link CommandPage}
 */
public class CommandPageTest extends AbstractGraphTablePageTest {

	@Override
	protected AbstractGraphTreePage createPage() {
		return new CommandPage();
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor accessor = new CommandDataAccessor();

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

}
