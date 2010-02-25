package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class FilePageTest extends ResourcePageTest {

	@Override
	protected ResourcePage createPage() {
		return new FilePage();
	}

	@Override
	public void testGetValue() throws Exception {
		// Even though the resource has a value > 0,
		// but because the page is for displaying files only, so 0 is
		// returned.

		long value = 9823;
		Map<IProject, Set<IResource>> projectResources = getFieldProjectResources(page);
		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		Map<IFile, Long> values = getFieldFileValues(page);

		IProject project = root.getProject("p");
		IFolder folder = project.getFolder("f");
		IFile file = folder.getFile("a");

		values.put(file, value);

		Set<IFile> files = new HashSet<IFile>();
		files.add(file);
		folderFiles.put(folder, files);

		Set<IResource> resources = new HashSet<IResource>();
		resources.add(folder);
		projectResources.put(project, resources);

		assertEquals(value, page.getValue(file));
		assertEquals(0, page.getValue(folder));
		assertEquals(0, page.getValue(project));
	}
}
