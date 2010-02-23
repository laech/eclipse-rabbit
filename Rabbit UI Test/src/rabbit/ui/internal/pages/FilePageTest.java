package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.ColumnComparator;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

public class FilePageTest extends AbstractGraphTreePageTest {

	@Override
	protected AbstractGraphTreePage createPage() {
		return new FilePage();
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor accessor = new FileDataAccessor();

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
		// Even though the resource has a value > 0,
		// but because the page is for displaying files only, so 0 is
		// returned.

		long value = 1098;
		FileElement file = new FileElement(Path.fromPortableString("/p/f/f.txt"), value);
		FolderElement folder = new FolderElement(Path.fromPortableString("/p/f"));
		folder.insert(file);
		ProjectElement project = new ProjectElement(Path.fromPortableString("/p"));
		project.insert(folder);

		Collection<ResourceElement> data = getData((FilePage) page);
		data.add(project);
		data.add(folder);
		data.add(file);

		assertEquals(0, page.getValue(project));
		assertEquals(0, page.getValue(folder));
		assertEquals(value, page.getValue(file));
		assertEquals(0, page.getValue(new Object()));
	}

	@Test
	public void testComparator() {
		FileElement file = new FileElement(Path.fromPortableString("/p/z.txt"), 10);
		FolderElement folder = new FolderElement(Path.fromPortableString("/p/a"));
		ColumnComparator sorter = page.createComparator(page.getViewer());
		assertTrue(sorter.compare(page.getViewer(), file, folder) < 0);
		assertTrue(sorter.compare(page.getViewer(), folder, file) > 0);
	}

	@SuppressWarnings("unchecked")
	protected Collection<ResourceElement> getData(FilePage page) throws Exception {
		Field field = FilePage.class.getDeclaredField("data");
		field.setAccessible(true);
		return (Collection<ResourceElement>) field.get(page);
	}
}
