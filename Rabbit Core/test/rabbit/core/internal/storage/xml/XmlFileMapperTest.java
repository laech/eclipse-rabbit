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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import rabbit.core.internal.RabbitCorePlugin;
import rabbit.core.internal.storage.xml.schema.resources.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.resources.ResourceListType;
import rabbit.core.internal.storage.xml.schema.resources.ResourceType;

/**
 * Test {@link XmlFileMapper}
 */
public class XmlFileMapperTest {

	private XmlFileMapper manager = XmlFileMapper.INSTANCE;

	private IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	@Before
	public void setUp() throws Exception {
		if (!getDataFile().delete()) {
			System.err.println("File is not deleted.");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertToMap() throws Exception {
		Method convert = XmlFileMapper.class.getDeclaredMethod("convert",
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
		Method convert = XmlFileMapper.class.getDeclaredMethod("convert", Map.class);
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
	public void testFileRenameEvent() throws Exception {
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
		IFile oldFile = folder.getFile("Hello.txt");
		if (!oldFile.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp", "txt"));
			oldFile.create(stream, true, null);
			stream.close();
		}
		manager.insert(oldFile);
		String oldId = manager.getId(oldFile);
		assertNotNull(oldId);

		IPath newPath = folder.getFullPath().append(System.currentTimeMillis() + "");
		oldFile.move(newPath, true, null);
		IFile newFile = root.getFile(newPath);
		assertEquals(oldId, manager.getId(newFile));

		// Old values must be removed:
		assertNull(manager.getId(oldFile));
	}

	@Test
	public void testFileRenameToDeletedName() throws Exception {
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
		String file1Id = manager.insert(file1);
		file1.delete(true, null);

		// Insert file2 into the database,
		// then rename file2 to become the deleted file1:
		String file2Id = manager.insert(file2);
		file2.move(file1.getFullPath(), true, null);

		// Now the two IDs should point to the same path:
		assertEquals(manager.getFile(file1Id), manager.getFile(file2Id));
		assertEquals(file1, manager.getFile(file1Id));
	}

	@Test
	public void testGetExternalFile() throws Exception {
		IFile file = root.getFile(Path.fromPortableString("/p/file.txt"));
		String id = System.nanoTime() + "." + System.currentTimeMillis();

		ObjectFactory of = new ObjectFactory();
		ResourceType type = of.createResourceType();
		type.setPath(file.getFullPath().toPortableString());
		type.getResourceId().add(id);
		ResourceListType resources = of.createResourceListType();
		resources.getResource().add(type);

		IPath filePath = RabbitCorePlugin.getDefault().getStoragePathRoot();
		filePath = filePath.append(System.currentTimeMillis() + "");

		Method getDataFile = XmlFileMapper.class.getDeclaredMethod(
				"getDataFile", IPath.class);
		getDataFile.setAccessible(true);
		File dataFile = (File) getDataFile.invoke(manager, filePath);

		Method marshal = XmlFileMapper.class.getDeclaredMethod(
				"marshal", JAXBElement.class, File.class);
		marshal.setAccessible(true);
		marshal.invoke(manager, of.createResources(resources), dataFile);

		// Test not to update the external resource:
		assertTrue(manager.write(false));
		assertNull(manager.getExternalFile(id));

		// Test to make sure write() == write(false):
		assertTrue(manager.write());
		assertNull(manager.getExternalFile(id));

		// Test to update the external resource:
		assertTrue(manager.write(true));
		assertEquals(file, manager.getExternalFile(id));
	}

	@Test
	public void testGetExternalFile_illegalArgument() throws Exception {

		// This is an illegal path for a file, because it's < 2 segments long.
		// We use this for testing:
		String path = "/123.txt";
		String id = System.nanoTime() + "." + System.currentTimeMillis();

		ObjectFactory of = new ObjectFactory();
		ResourceType type = of.createResourceType();
		type.setPath(path);
		type.getResourceId().add(id);
		ResourceListType resources = of.createResourceListType();
		resources.getResource().add(type);

		IPath filePath = RabbitCorePlugin.getDefault().getStoragePathRoot();
		filePath = filePath.append(System.currentTimeMillis() + "");

		Method getDataFile = XmlFileMapper.class.getDeclaredMethod(
				"getDataFile", IPath.class);
		getDataFile.setAccessible(true);
		File dataFile = (File) getDataFile.invoke(manager, filePath);

		Method marshal = XmlFileMapper.class.getDeclaredMethod(
				"marshal", JAXBElement.class, File.class);
		marshal.setAccessible(true);
		marshal.invoke(manager, of.createResources(resources), dataFile);

		// Set true to update:
		assertTrue(manager.write(true));
		// Should return null instead of throwing an exception when an illegal path is met:
		try {
			assertNull(null, manager.getExternalFile(id));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testGetFile() {
		String id = System.nanoTime() + "" + System.currentTimeMillis();
		assertNull(manager.getFile(id));
	}

	@Test
	public void testGetId() {
		IFile file = root.getProject("p").getFile(System.currentTimeMillis() + "");
		assertNull(manager.getId(file));
	}

	@Test
	public void testInsert() {
		IFile file = root.getProject("p").getFile(System.currentTimeMillis() + "");
		assertNull(manager.getId(file));

		String id = manager.insert(file);
		assertNotNull(id);
		assertEquals(id, manager.getId(file));
	}

	@Test
	public void testPostShutdown() throws Exception {
		assertFalse(getDataFile().exists());
		manager.postShutdown(PlatformUI.getWorkbench());
		assertTrue(getDataFile().exists());
	}

	@Test
	public void testShutdown() throws Exception {
		assertFalse(getDataFile().exists());
		PlatformUI.getWorkbench().close();
		assertTrue(getDataFile().exists());
	}

	@Test
	public void testWrite() throws Exception {
		IFile file = root.getProject("p").getFile(System.nanoTime() + "");
		assertNull(manager.getId(file));
		String id = manager.insert(file);

		assertTrue(manager.write());

		Method getData = XmlFileMapper.class.getDeclaredMethod("getData");
		getData.setAccessible(true);
		ResourceListType resources = (ResourceListType) getData.invoke(manager);

		for (ResourceType type : resources.getResource()) {
			if (type.getPath().equals(file.getFullPath().toPortableString())) {
				if (type.getResourceId().size() != 1) {
					fail();
				}
				if (!type.getResourceId().get(0).equals(id)) {
					fail();
				}
			}
		}

		getResourcesFiled().clear();
		id = manager.insert(file);
		assertTrue(manager.write());
		resources = (ResourceListType) getData.invoke(manager);

		for (ResourceType type : resources.getResource()) {
			if (type.getPath().equals(file.getFullPath().toPortableString())) {
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

	@Test
	public void testWrite_backup() throws Exception {
		IFile file = root.getProject("p").getFile(System.nanoTime() + "");
		assertNull(manager.getId(file));
		String id = manager.insert(file);

		assertTrue(manager.write());

		Method getData = XmlFileMapper.class.getDeclaredMethod("getData", File.class);
		getData.setAccessible(true);
		ResourceListType resources = (ResourceListType) getData.invoke(manager, getBackupFile());
		for (ResourceType type : resources.getResource()) {
			if (type.getPath().equals(file.getFullPath().toPortableString())) {
				if (type.getResourceId().size() != 1) {
					fail();
				}
				if (!type.getResourceId().get(0).equals(id)) {
					fail();
				}
			}
		}
	}

	private File getBackupFile() throws Exception {
		Method method = XmlFileMapper.class.getDeclaredMethod("getBackupFile");
		method.setAccessible(true);
		File file = (File) method.invoke(manager);
		return file;
	}

	private File getDataFile() throws Exception {
		Method dataFile = XmlFileMapper.class.getDeclaredMethod("getDataFile");
		dataFile.setAccessible(true);
		File file = (File) dataFile.invoke(manager);
		return file;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Set<String>> getResourcesFiled() throws Exception {
		Field field = XmlFileMapper.class.getDeclaredField("resources");
		field.setAccessible(true);
		return (Map<String, Set<String>>) field.get(manager);
	}
}
