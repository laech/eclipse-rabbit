/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.RabbitCore;
import rabbit.core.TestUtil;
import rabbit.core.internal.storage.xml.schema.resources.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.resources.ResourceListType;
import rabbit.core.internal.storage.xml.schema.resources.ResourceType;

/**
 * Test {@link XmlResourceManager}
 */
public class XmlResourceManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtil.setUpPathForTesting();
	}

	private XmlResourceManager manager = XmlResourceManager.INSTANCE;

	@Before
	public void setUp() throws Exception {
		if (!getDataFile().delete()) {
			System.err.println("File is not deleted.");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertToMap() throws Exception {
		Method convert = XmlResourceManager.class.getDeclaredMethod("convert",
				ResourceListType.class);
		convert.setAccessible(true);

		ObjectFactory of = new ObjectFactory();
		ResourceListType resources = of.createResourceListType();

		ResourceType type1 = of.createResourceType();
		type1.setPath("path1");
		type1.getResourceId().add("id1.1");
		type1.getResourceId().add("id1.2");
		resources.getResource().add(type1);

		ResourceType type2 = of.createResourceType();
		type2.setPath("path2");
		type2.getResourceId().add("id2.1");
		resources.getResource().add(type2);

		Map<String, Set<String>> map = (Map<String, Set<String>>) convert
				.invoke(manager, resources);
		assertEquals(2, map.size());

		Set<String> set1 = map.get(type1.getPath());
		assertNotNull(set1);
		assertEquals(2, set1.size());
		assertTrue(set1.contains("id1.1"));
		assertTrue(set1.contains("id1.2"));

		Set<String> set2 = map.get(type2.getPath());
		assertNotNull(set2);
		assertEquals(1, set2.size());
		assertTrue(set2.contains("id2.1"));
	}

	@Test
	public void testConvertToType() throws Exception {
		Method convert = XmlResourceManager.class.getDeclaredMethod("convert", Map.class);
		convert.setAccessible(true);

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		Set<String> set1 = new HashSet<String>();
		set1.add("id1.1");
		set1.add("id1.2");
		set1.add("id1.3");
		map.put("path1", set1);

		Set<String> set2 = new HashSet<String>();
		set2.add("id2.1");
		set2.add("id2.2");
		map.put("path2", set2);

		ResourceListType resources = (ResourceListType) convert.invoke(manager, map);
		assertEquals(2, resources.getResource().size());

		ResourceType type1 = resources.getResource().get(0);
		if (type1.getPath().equals("path1") == false) {
			type1 = resources.getResource().get(1);
		}
		assertEquals("path1", type1.getPath());
		assertEquals(3, type1.getResourceId().size());
		assertTrue(type1.getResourceId().contains("id1.1"));
		assertTrue(type1.getResourceId().contains("id1.2"));
		assertTrue(type1.getResourceId().contains("id1.3"));

		ResourceType type2 = resources.getResource().get(1);
		if (type2.getPath().equals("path2") == false) {
			type2 = resources.getResource().get(0);
		}
		assertEquals("path2", type2.getPath());
		assertEquals(2, type2.getResourceId().size());
		assertTrue(type2.getResourceId().contains("id2.1"));
		assertTrue(type2.getResourceId().contains("id2.2"));
	}

	@Test
	public void testGetExternalPath() throws Exception {
		String path = System.currentTimeMillis() + "";
		String id = System.nanoTime() + "";

		ObjectFactory of = new ObjectFactory();
		ResourceType type = of.createResourceType();
		type.setPath(path);
		type.getResourceId().add(id);
		ResourceListType resources = of.createResourceListType();
		resources.getResource().add(type);

		IPath filePath = new Path(RabbitCore.getDefault().getPreferenceStore().getString(
				RabbitCore.STORAGE_LOCATION));
		filePath = filePath.append(System.currentTimeMillis() + "");

		Method getDataFile = XmlResourceManager.class.getDeclaredMethod("getDataFile", IPath.class);
		getDataFile.setAccessible(true);
		File dataFile = (File) getDataFile.invoke(manager, filePath);

		Method marshal = XmlResourceManager.class.getDeclaredMethod("marshal", JAXBElement.class,
				File.class);
		marshal.setAccessible(true);
		marshal.invoke(manager, of.createResources(resources), dataFile);

		manager.write(true);
		assertEquals(path, manager.getExternalPath(id));
	}

	@Test
	public void testGetFilePath() {
		String id = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(manager.getPath(id));
	}

	@Test
	public void testGetId() {
		String path = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(manager.getId(path));
	}

	@Test
	public void testInsert() {
		String path = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(manager.getId(path));
		assertNotNull(manager.insert(path));
		assertNotNull(manager.getId(path));
	}

	@Test
	public void testPostShutdown() throws Exception {
		assertFalse(getDataFile().exists());
		manager.postShutdown(PlatformUI.getWorkbench());
		assertTrue(getDataFile().exists());
	}

	@Test
	public void testRenameEvent_withFile() throws Exception {
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
		IFile file = folder.getFile("Hello.txt");
		if (!file.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp", "txt"));
			file.create(stream, true, null);
			stream.close();
		}
		IPath oldPath = file.getFullPath();
		manager.insert(oldPath.toString());
		String id = manager.getId(oldPath.toString());
		assertNotNull(id);

		IPath newPath = folder.getFullPath().append(System.currentTimeMillis() + "");
		file.move(newPath, true, null);
		assertEquals(id, manager.getId(newPath.toString()));

		// Old values must be removed:
		assertNull(manager.getId(oldPath.toString()));
	}

	@Test
	public void testRenameEvent_withFolder() throws CoreException {
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
		IPath oldPath = folder.getFullPath();
		manager.insert(oldPath.toString());
		String id = manager.getId(oldPath.toString());
		assertNotNull(id);

		IPath newPath = project.getFullPath().append(System.currentTimeMillis() + "");
		folder.move(newPath, true, null);
		assertEquals(id, manager.getId(newPath.toString()));

		// Old values must be removed:
		assertNull(manager.getId(oldPath.toString()));
	}

	@Test
	public void testRenameEvent_withProject() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		IPath oldPath = project.getFullPath();
		manager.insert(oldPath.toString());
		String id = manager.getId(oldPath.toString());
		assertNotNull(id);

		IPath newPath = Path.fromPortableString("/" + System.currentTimeMillis());
		project.move(newPath, true, null);
		assertEquals(id, manager.getId(newPath.toString()));

		// Old values must be removed:
		assertNull(manager.getId(oldPath.toString()));
	}

	@Test
	public void testRenameToDeletedName_withFile() throws Exception {
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
	}

	@Test
	public void testRenameToDeletedName_withFolder() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}

		IFolder folder1 = project.getFolder("folder1");
		if (!folder1.exists()) {
			folder1.create(true, true, null);
		}
		IFolder folder2 = project.getFolder("folder2");
		if (!folder2.exists()) {
			folder2.create(true, true, null);
		}

		// Insert folder1 into the database, then delete it from workspace:
		String folder1Id = manager.insert(folder1.getFullPath().toString());
		folder1.delete(true, null);

		// Insert folder2 into the database,
		// then rename folder2 to become the deleted folder1:
		String folder2Id = manager.insert(folder2.getFullPath().toString());
		folder2.move(folder1.getFullPath(), true, null);

		// Now the two IDs should point to the same path:
		assertEquals(manager.getPath(folder1Id), manager.getPath(folder2Id));
		assertEquals(folder1.getFullPath().toString(), manager.getPath(folder1Id));
	}

	@Test
	public void testRenameToDeletedName_withProject() throws Exception {
		IProject project1 = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp1");
		if (!project1.exists()) {
			project1.create(null);
		}
		if (!project1.isOpen()) {
			project1.open(null);
		}

		IProject project2 = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp2");
		if (!project2.exists()) {
			project2.create(null);
		}
		if (!project2.isOpen()) {
			project2.open(null);
		}

		// Insert project1 into the database, then delete it from workspace:
		String project1Id = manager.insert(project1.getFullPath().toString());
		project1.delete(true, null);

		// Insert project2 into the database,
		// then rename project2 to become the deleted project1:
		String project2Id = manager.insert(project2.getFullPath().toString());
		project2.move(project1.getFullPath(), true, null);

		// Now the two IDs should point to the same path:
		assertEquals(manager.getPath(project1Id), manager.getPath(project2Id));
		assertEquals(project1.getFullPath().toString(), manager.getPath(project1Id));
		assertEquals(project1.getFullPath().toString(), manager.getPath(project2Id));
	}

	@Test
	public void testShutdown() throws Exception {
		assertFalse(getDataFile().exists());
		PlatformUI.getWorkbench().close();
		assertTrue(getDataFile().exists());
	}

	@Test
	public void testWrite() throws Exception {
		String path = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(manager.getId(path));
		String id = manager.insert(path);

		manager.write();

		Method getData = XmlResourceManager.class.getDeclaredMethod("getData");
		getData.setAccessible(true);
		ResourceListType resources = (ResourceListType) getData.invoke(manager);
		for (ResourceType type : resources.getResource()) {
			if (type.getPath().equals(path)) {
				if (type.getResourceId().size() != 1) {
					fail();
				}
				if (!type.getResourceId().get(0).equals(id)) {
					fail();
				}
			}
		}

		getResourcesFiled().clear();
		id = manager.insert(path);
		manager.write();
		resources = (ResourceListType) getData.invoke(manager);
		for (ResourceType type : resources.getResource()) {
			if (type.getPath().equals(path)) {
				if (type.getResourceId().size() != 1) {
					fail();
				}
				if (type.getResourceId().get(0).equals(id)) {
					return;
				}
				fail();
			}
		}

		fail();
	}

	private File getDataFile() throws Exception {
		Method dataFile = XmlResourceManager.class.getDeclaredMethod("getDataFile");
		dataFile.setAccessible(true);
		File file = (File) dataFile.invoke(manager);
		return file;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Set<String>> getResourcesFiled() throws Exception {
		Field field = XmlResourceManager.class.getDeclaredField("resources");
		field.setAccessible(true);
		return (Map<String, Set<String>>) field.get(manager);
	}
}
