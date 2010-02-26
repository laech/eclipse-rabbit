package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IResourceManager;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.DisplayPreference;

public abstract class ResourcePageTest {

	protected static IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	protected ResourcePage page;

	private static Shell shell;

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@Before
	public void before() {
		page = createPage();
		page.createContents(shell);
	}

	@Test
	public void testUpdate() {
		Map<IProject, Long> projects = new HashMap<IProject, Long>();
		Map<IFolder, Long> folders = new HashMap<IFolder, Long>();
		Map<IFile, Long> files = new HashMap<IFile, Long>();

		DisplayPreference preference = new DisplayPreference();
		Calendar end = preference.getEndDate();
		preference.getStartDate().set(end.get(Calendar.YEAR), end.get(Calendar.MONTH) - 2, end.get(Calendar.DAY_OF_MONTH));

		IAccessor accessor = new FileDataAccessor();
		Map<String, Long> data = accessor.getData(preference.getStartDate(), preference.getEndDate());

		IResourceManager mapper = RabbitCore.getDefault().getResourceManager();
		for (Entry<String, Long> entry : data.entrySet()) {
			String path = mapper.getPath(entry.getKey());
			if (path == null) {
				continue;
			}

			IFile file = root.getFile(Path.fromPortableString(path));
			Long oldValue = files.get(file);
			if (oldValue == null) {
				oldValue = Long.valueOf(0);
			}
			files.put(file, oldValue + entry.getValue());

			IProject project = file.getProject();
			oldValue = projects.get(project);
			if (oldValue == null) {
				oldValue = Long.valueOf(0);
			}
			projects.put(project, oldValue + entry.getValue());

			IContainer parent = file.getParent();
			if (parent != project) {
				IFolder folder = (IFolder) parent;
				oldValue = folders.get(parent);
				if (oldValue == null) {
					oldValue = Long.valueOf(0);
				}
				folders.put(folder, oldValue + entry.getValue());
			}
		}

		page.update(preference);
		for (Entry<IProject, Long> entry : projects.entrySet()) {
			assertEquals(entry.getValue().longValue(), page.getValueOfProject(entry.getKey()));
		}
		for (Entry<IFolder, Long> entry : folders.entrySet()) {
			assertEquals(entry.getValue().longValue(), page.getValueOfFolder(entry.getKey()));
		}
		for (Entry<IFile, Long> entry : files.entrySet()) {
			assertEquals(entry.getValue().longValue(), page.getValueOfFile(entry.getKey()));
		}
	}

	@Test
	public void testDoUpdate() throws Exception {
		// Test two id pointing to same file, getting the value of the file must
		// return the sum.

		IResourceManager manager = RabbitCore.getDefault().getResourceManager();
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		IFolder folder = project.getFolder("folder");
		if (!folder.exists()) {
			folder.create(true, true, null);
		}

		IFile file1 = folder.getFile("Hello1.txt");
		if (!file1.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp1", "txt"));
			file1.create(stream, true, null);
			stream.close();
		}
		IFile file2 = folder.getFile("Hello2.txt");
		if (!file2.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp2", "txt"));
			file2.create(stream, true, null);
			stream.close();
		}

		// Insert file1 into the database, then delete it from workspace:
		String file1Id = manager.insert(file1.getFullPath().toString());
		file1.delete(true, null);

		// Insert file2 into the database,
		// then rename file2 to become the deleted file1:
		String file2Id = manager.insert(file2.getFullPath().toString());
		file2.move(file1.getFullPath(), true, null);

		// Now the two IDs should point to the same path:
		assertEquals(manager.getPath(file1Id), manager.getPath(file2Id));
		assertEquals(file1.getFullPath().toString(), manager.getPath(file1Id));
		
		long value1 = 19084;
		long value2 = 28450;

		Map<String, Long> data = new HashMap<String, Long>();
		data.put(file1Id, value1);
		data.put(file2Id, value2);
		doUpdate(page, data);

		assertEquals(value1 + value2, page.getValueOfFile(file1));
		assertEquals(value1 + value2, page.getValueOfFolder(folder));
		assertEquals(value1 + value2, page.getValueOfProject(project));
	}

	@Test
	public void testGetValueOfProject() throws Exception {
		Map<IProject, Set<IResource>> projectResources = getFieldProjectResources(page);
		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		Map<IFile, Long> values = getFieldFileValues(page);

		final long value = 9676219;
		IProject project = root.getProject("p");

		IFolder folder1 = project.getFolder("f1");
		{
			IFile file1 = folder1.getFile("a");
			IFile file2 = folder1.getFile("b");
			values.put(file1, value);
			values.put(file2, value);

			Set<IFile> files = new HashSet<IFile>(2, 1);
			files.add(file1);
			files.add(file2);
			folderFiles.put(folder1, files);
		}
		IFolder folder2 = project.getFolder("f2");
		{
			IFile file1 = folder2.getFile("c");
			IFile file2 = folder2.getFile("d");
			values.put(file1, value);
			values.put(file2, value);

				Set<IFile> files = new HashSet<IFile>(2, 1);
			files.add(file1);
			files.add(file2);
			folderFiles.put(folder2, files);
		}

		Set<IResource> folders = new HashSet<IResource>(2, 1);
		folders.add(folder1);
		folders.add(folder2);
		projectResources.put(project, folders);

		assertEquals(value * 4, page.getValueOfProject(project));
	}

	@Test
	public void testGetValueOfFolder() throws Exception {
		IFolder folder = root.getFolder(Path.fromPortableString("/p/f"));

		long value1 = 2938;
		long value2 = 123999;
		IFile file1 = folder.getFile("a");
		IFile file2 = folder.getFile("b");

		Map<IFile, Long> values = getFieldFileValues(page);
		values.put(file1, value1);
		values.put(file2, value2);

		Set<IFile> files = new HashSet<IFile>(2, 1);
		files.add(file1);
		files.add(file2);

		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		folderFiles.put(folder, files);

		assertEquals(value1 + value2, page.getValueOfFolder(folder));
	}

	@Test
	public void testGetValueOfFile() throws Exception {
		long value = 18332;
		IFile file = root.getFile(Path.fromPortableString("/p/a"));
		
		Map<IFile, Long> values = getFieldFileValues(page);
		values.put(file, value);
		
		assertEquals(value, page.getValueOfFile(file));
	}

	@Test
	public void getResources() throws Exception {
		IProject project = root.getProject("p");
		IFolder folder = project.getFolder("f");
		IFile file = folder.getFile("a");

		Set<IResource> resources = new HashSet<IResource>(2, 1);
		resources.add(folder);
		resources.add(file);

		Map<IProject, Set<IResource>> projectResources = getFieldProjectResources(page);
		projectResources.put(project, resources);

		IResource[] array = page.getResources(project);
		assertEquals(resources.size(), array.length);
		assertTrue(resources.contains(array[0]));
		assertTrue(resources.contains(array[1]));
	}

	@Test
	public void testGetMaxProjectValue() throws Exception {
		Map<IProject, Set<IResource>> projectResources = getFieldProjectResources(page);
		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		Map<IFile, Long> values = getFieldFileValues(page);

		final long value = 9676219;
		for (int i = 0; i < 3; i++) {
			IProject project = root.getProject("p");

			IFolder folder1 = project.getFolder("f1");
			{
				IFile file1 = folder1.getFile("a");
				IFile file2 = folder1.getFile("b");
				values.put(file1, value);
				values.put(file2, value);

				Set<IFile> files = new HashSet<IFile>(2, 1);
				files.add(file1);
				files.add(file2);
				folderFiles.put(folder1, files);
			}
			IFolder folder2 = project.getFolder("f2");
			{
				IFile file1 = folder2.getFile("c");
				IFile file2 = folder2.getFile("d");
				values.put(file1, value);
				values.put(file2, value);

				Set<IFile> files = new HashSet<IFile>(2, 1);
				files.add(file1);
				files.add(file2);
				folderFiles.put(folder2, files);
			}

			Set<IResource> folders = new HashSet<IResource>(2, 1);
			folders.add(folder1);
			folders.add(folder2);
			projectResources.put(project, folders);
		}
		assertEquals(value * 4, page.getMaxProjectValue());
	}

	@Test
	public void testGetMaxFolderValue() throws Exception {
		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		Map<IFile, Long> values = getFieldFileValues(page);

		final long value = 877565464;
		for (int i = 0; i < 3; i++) {
			IFolder folder = root.getFolder(Path.fromPortableString("/p/" + i));

			IFile file1 = folder.getFile("a");
			IFile file2 = folder.getFile("b");
			values.put(file1, value - i);
			values.put(file2, value - i);

			Set<IFile> files = new HashSet<IFile>(2, 1);
			files.add(file1);
			files.add(file2);
			folderFiles.put(folder, files);
		}

		assertEquals(value * 2, page.getMaxFolderValue());
	}

	@Test
	public void testGetMaxFileValue() throws Exception {
		final long maxValue = 10998;
		Map<IFile, Long> values = getFieldFileValues(page);
		for (int i = 0; i < 2; i++) {
			values.put(root.getFile(Path.fromPortableString("/p/" + i)), maxValue - i);
		}
		assertEquals(maxValue, page.getMaxFileValue());
	}

	@Test
	public void testGetFiles() throws Exception {
		IFolder folder = root.getFolder(Path.fromPortableString("/p/f"));

		IFile file1 = folder.getFile("a");
		IFile file2 = folder.getFile("b");
		Set<IFile> files = new HashSet<IFile>(2, 1);
		files.add(file1);
		files.add(file2);

		Map<IFolder, Set<IFile>> folderFiles = getFieldFolderFiles(page);
		folderFiles.put(folder, files);

		IFile[] array = page.getFiles(folder);
		assertEquals(files.size(), array.length);
		assertTrue(files.contains(array[0]));
		assertTrue(files.contains(array[1]));
	}

	@SuppressWarnings("unchecked")
	static Map<IProject, Set<IResource>> getFieldProjectResources(ResourcePage page) throws Exception {
		Field field = ResourcePage.class.getDeclaredField("projectResources");
		field.setAccessible(true);
		return (Map<IProject, Set<IResource>>) field.get(page);
	}

	@SuppressWarnings("unchecked")
	static Map<IFolder, Set<IFile>> getFieldFolderFiles(ResourcePage page) throws Exception {
		Field field = ResourcePage.class.getDeclaredField("folderFiles");
		field.setAccessible(true);
		return (Map<IFolder, Set<IFile>>) field.get(page);
	}

	@SuppressWarnings("unchecked")
	static Map<IFile, Long> getFieldFileValues(ResourcePage page) throws Exception {
		Field field = ResourcePage.class.getDeclaredField("fileValues");
		field.setAccessible(true);
		return (Map<IFile, Long>) field.get(page);
	}

	static void doUpdate(ResourcePage page, Map<String, Long> data) throws Exception {
		Method method = ResourcePage.class.getDeclaredMethod("doUpdate", Map.class);
		method.setAccessible(true);
		method.invoke(page, data);
	}

	@Test
	public abstract void testGetValue() throws Exception;

	protected abstract ResourcePage createPage();
}
