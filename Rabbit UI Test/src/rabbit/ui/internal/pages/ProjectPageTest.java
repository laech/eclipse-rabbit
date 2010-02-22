package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.junit.Test;

import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

/**
 * Test for {@link ProjectPage}
 */
public class ProjectPageTest extends AbstractGraphTreePageTest {

	@Override
	protected ProjectPage createPage() {
		return new ProjectPage();
	}

	@Test
	public void testContentProvider() throws Exception {
		ITreeContentProvider provider = page.createContentProvider();

		Collection<Object> objects = new ArrayList<Object>();
		objects.add(new Object());
		assertTrue(provider.hasChildren(objects));

		ResourceElement resource = new ProjectElement(Path.fromPortableString("/project"));
		assertFalse(provider.hasChildren(resource));

		resource = new FolderElement(Path.fromPortableString("/project/folder"));
		assertFalse(provider.hasChildren(resource));

		resource = new FileElement(Path.fromPortableString("project/folder/file.txt"), 10);
		assertFalse(provider.hasChildren(resource));
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		DisplayPreference pref = new DisplayPreference();
		page.update(pref);
		for (ResourceElement project : getData((FilePage) page)) { // projects
			if (project.getValue() > max) {
				max = project.getValue();
			}
		}
		assertEquals(max, page.getMaxValue());

		pref.getStartDate().add(Calendar.MONTH, -1);
		pref.getEndDate().add(Calendar.DAY_OF_MONTH, -5);
		page.update(pref);
		max = 0;
		for (ResourceElement project : getData((FilePage) page)) { // projects
			if (project.getValue() > max) {
				max = project.getValue();
			}
		}
		assertEquals(max, page.getMaxValue());
	}

	@Test
	public void testGetValue() throws Exception {
		// Even though the resource has a value > 0,
		// but because the page is for displaying projects only, so 0 is
		// returned.

		long value = 1098;
		FileElement file = new FileElement(Path.fromPortableString("/p/f/f.txt"), value);
		FolderElement folder = new FolderElement(Path.fromPortableString("/p/f"));
		folder.insert(file);
		ProjectElement project = new ProjectElement(Path.fromPortableString("/p"));
		project.insert(folder);

		Collection<ResourceElement> data = getData((FilePage) page);
		data.clear();
		data.add(project);
		data.add(folder);
		data.add(file);

		assertEquals(1098, page.getValue(project));
		assertEquals(0, page.getValue(folder));
		assertEquals(0, page.getValue(file));
		assertEquals(0, page.getValue(new Object()));
	}

	@SuppressWarnings("unchecked")
	protected Collection<ResourceElement> getData(FilePage page) throws Exception {
		Field field = FilePage.class.getDeclaredField("data");
		field.setAccessible(true);
		return (Collection<ResourceElement>) field.get(page);
	}
}
