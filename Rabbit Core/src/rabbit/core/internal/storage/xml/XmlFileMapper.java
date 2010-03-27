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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import rabbit.core.internal.RabbitCorePlugin;
import rabbit.core.internal.storage.xml.schema.resources.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.resources.ResourceListType;
import rabbit.core.internal.storage.xml.schema.resources.ResourceType;
import rabbit.core.storage.IFileMapper;

/**
 * An XML {@link IFileMapper}.
 */
public enum XmlFileMapper
		implements IFileMapper, IResourceChangeListener, IWorkbenchListener {

	INSTANCE;

	private JAXBContext jaxb;
	private Unmarshaller unmar;
	private Marshaller mar;
	private ObjectFactory objectFactory;
	private Random random;

	/** Map of file path to ids of current workspace. */
	private Map<String, Set<String>> resources;
	/** Set of all the ids in the {@link #resources} field. */
	private Set<String> resourceIds;

	/** Map of file path to ids, from other workspaces, not for manipulation. */
	private Map<String, Set<String>> externalResources;

	private IResourceDeltaVisitor renameVisitor = new IResourceDeltaVisitor() {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {

			if ((delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) {
				return true;
			}
			IResource resource = delta.getResource();

			String oldPath = delta.getMovedFromPath().toString();
			Set<String> oldIds = resources.get(oldPath);
			if (oldIds == null) {
				return true;
			}

			String newPath = resource.getFullPath().toString();
			Set<String> newIds = resources.get(newPath);
			if (newIds == null) {
				newIds = new HashSet<String>();
				resources.put(newPath, newIds);
			}
			newIds.addAll(oldIds);
			resources.remove(oldPath);
			return true;
		}
	};

	/**
	 * Formats a date to the form "yyyy-MM".
	 */
	private final DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

	private IWorkspaceRoot workspaceRoot =  ResourcesPlugin.getWorkspace().getRoot();
	
	/** Constructor. */
	private XmlFileMapper() {
		try {
			jaxb = JAXBContext.newInstance(ObjectFactory.class);
			mar = jaxb.createMarshaller();
			unmar = jaxb.createUnmarshaller();
		} catch (JAXBException e) {
			try {
				jaxb = JAXBContext.newInstance(ObjectFactory.class);
				mar = jaxb.createMarshaller();
				unmar = jaxb.createUnmarshaller();
			} catch (JAXBException ex) {
				ex.printStackTrace();
				return;
			}
		}

		objectFactory = new ObjectFactory();
		random = new Random();

		resources = convert(getData());
		resourceIds = new HashSet<String>(resources.size() * 2);
		for (Set<String> ids : resources.values()) {
			resourceIds.addAll(ids);
		}

		externalResources = getExternalResources();
	}
	
	@Override
	public IFile getExternalFile(String fileId) {
		for (Entry<String, Set<String>> entry : externalResources.entrySet()) {
			for (String id : entry.getValue()) {
				if (id.equals(fileId)) {
					try {
						return workspaceRoot.getFile(new Path(entry.getKey()));
					} catch (Exception e) {
						System.err.println(e.getMessage());
						return null;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public IFile getFile(String id) {
		if (!resourceIds.contains(id)) {
			return null;
		}
		for (Map.Entry<String, Set<String>> entry : resources.entrySet()) {
			for (String str : entry.getValue()) {
				if (str.equals(id)) {
					try {
						return workspaceRoot.getFile(new Path(entry.getKey()));
					} catch (Exception e) {
						System.err.println(e.getMessage());
						return null;
					}
				}
			}
		}
		// Should not arrive here:
		throw new AssertionFailedException("Bug?");
	}
	
	@Override
	public String getId(IFile file) {
		Set<String> ids = resources.get(getPathString(file));
		if (ids != null) {
			return ids.iterator().next();
		}
		return null;
	}
	
	@Override
	public String insert(IFile file) {
		String id = getId(file);
		if (id == null) {
			id = generateId();
			while (resourceIds.contains(id)) {
				id = generateId();
			}
			
			String path = getPathString(file);
			Set<String> ids = resources.get(path);
			if (ids == null) {
				ids = new HashSet<String>();
				resources.put(path, ids);
			}
			ids.add(id);
			resourceIds.add(id);
		}
		return id;
	}
	
	@Override
	public void postShutdown(IWorkbench workbench) {
		if (!write()) {
			RabbitCorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR,
					RabbitCorePlugin.PLUGIN_ID, "Unable to save resource mappings."));
		}
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta == null) {
			return;
		}
		try {
			delta.accept(renameVisitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the current data to disk. Same as {@link #write(false)}.
	 * 
	 * @return {@code true} if data is successfully saved, {@code false}
	 *         otherwise.
	 */
	public boolean write() {
		try {
			marshal(objectFactory.createResources(convert(resources)), getDataFile());

			// Creates a backup silently.
			marshal(objectFactory.createResources(convert(resources)), getBackupFile());

			return true;
		} catch (JAXBException e) {
			RabbitCorePlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, RabbitCorePlugin.PLUGIN_ID,
							"Unable to save resource mappings.", e));
			return false;
		}
	}

	/**
	 * Saves the current data to disk.
	 * 
	 * @param update
	 *            True to update the references to external resources, false
	 *            otherwise.
	 * @return {@code true} if data is successfully saved, {@code false}
	 *         otherwise.
	 */
	public boolean write(boolean update) {
		boolean result = write();
		if (update) {
			externalResources = getExternalResources();
		}
		return result;
	}

	private ResourceListType convert(Map<String, Set<String>> v) {
		ResourceListType resources = objectFactory.createResourceListType();
		for (Map.Entry<String, Set<String>> entry : v.entrySet()) {
			ResourceType type = objectFactory.createResourceType();
			type.setPath(entry.getKey());
			type.getResourceId().addAll(entry.getValue());
			resources.getResource().add(type);
		}
		return resources;
	}

	private Map<String, Set<String>> convert(ResourceListType v) {
		Map<String, Set<String>> data = new TreeMap<String, Set<String>>();
		for (ResourceType type : v.getResource()) {
			Set<String> ids = new HashSet<String>(type.getResourceId().size() * 2);
			ids.addAll(type.getResourceId());
			data.put(type.getPath(), ids);
		}
		return data;
	}

	/**
	 * Generates a random id.
	 * 
	 * @return A random id.
	 */
	private String generateId() {
		return System.currentTimeMillis() + "" + random.nextInt();
	}

	/**
	 * Gets the file for writing a back up.
	 * 
	 * @return The back up file.
	 */
	private File getBackupFile() {

		StringBuilder builder = new StringBuilder(getDataFile().getParent())
				.append(File.separator)
				.append("Backup")
				.append(File.separator)
				.append(monthFormat.format(new Date()))
				.append(".xml");

		File backupFile = new File(builder.toString());
		if (!backupFile.getParentFile().exists()) {
			if (!backupFile.getParentFile().mkdirs()) {
				System.err.println(getClass() + ": Cannot create backup location.");
			}
		}
		return backupFile;
	}

	/**
	 * Gets the resource data of the current workspace.
	 * 
	 * @return The resource data of the current workspace
	 */
	private ResourceListType getData() {
		return getData(getDataFile());
	}

	/**
	 * Gets the data from the given file.
	 * 
	 * @param dataFile
	 *            The file containing the data.
	 * @return A {@link ResourceListType} with the data. Returns an empty one if
	 *         no data is in the file, or exceptions occur while processing the
	 *         file.
	 */
	private ResourceListType getData(File dataFile) {
		ResourceListType database = null;
		if (dataFile.exists()) {
			try {
				database = unmarshal(dataFile);
			} catch (JAXBException e) {
				database = objectFactory.createResourceListType();
			}
		}
		if (database == null) {
			database = objectFactory.createResourceListType();
		}
		return database;
	}

	/**
	 * Gets the data file belongs to the current workspace.
	 * 
	 * @return The data file of the current workspace.
	 */
	private File getDataFile() {
		return getDataFile(RabbitCorePlugin.getDefault().getStoragePath());
	}

	/**
	 * Gets the data file from the given location. Note that the data file may
	 * not physically exists.
	 * 
	 * @param storageLocation
	 *            The folder location.
	 * @return The data file.
	 */
	private File getDataFile(IPath storageLocation) {
		File file = storageLocation.append("ResourceDB")
				.append("Resources").addFileExtension("xml").toFile();

		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				RabbitCorePlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, RabbitCorePlugin.PLUGIN_ID,
								"Unable to create storage location. Perhaps no write permission?\n"
										+ file.getParent()));
			}
		}
		return file;
	}

	/**
	 * Gets the file path to ids mapping of other workspaces.
	 * 
	 * @return An unmodifiable map containing file path to ids.
	 */
	private Map<String, Set<String>> getExternalResources() {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();
		IPath currentPath = RabbitCorePlugin.getDefault().getStoragePath();
		IPath[] paths = RabbitCorePlugin.getDefault().getStoragePaths();
		for (IPath path : paths) {
			if (path.toOSString().equals(currentPath.toOSString())) {
				continue;
			}
			File file = getDataFile(path);

			for (Entry<String, Set<String>> entry : convert(getData(file)).entrySet()) {
				Set<String> ids = result.get(entry.getKey());
				if (ids == null) {
					ids = new HashSet<String>();
					result.put(entry.getKey(), ids);
				}
				ids.addAll(entry.getValue());
			}
		}

		return Collections.unmodifiableMap(result);
	}

	private String getPathString(IFile file) {
		return file.getFullPath().toPortableString();
	}

	/**
	 * Marshals a element to file.
	 * 
	 * @param element
	 *            The element.
	 * @param file
	 *            The file to write to.
	 * @return {@code true} if the element is successfully written to the file,
	 *         {@code false} otherwise.
	 * @throws JAXBException
	 *             If any unexpected problem occurs during the marshalling.
	 * @throws NullPointerException
	 *             If either parameters is null.
	 */
	private void marshal(JAXBElement<ResourceListType> element, File file) throws JAXBException {
		if (element == null || file == null) {
			throw new NullPointerException();
		}
		mar.marshal(element, file);
	}

	/**
	 * Unmarshals a file.
	 * 
	 * @param file
	 *            The file containing the data.
	 * @return The ResourceListType object from the file; or null, if the file
	 *         does not containing a JAXBElement object, or the JAXBElement
	 *         object does not contain a ResourceListType object.
	 * @throws JAXBException
	 *             If any unexpected errors occur while unmarshalling .
	 * @throws NullPointerException
	 *             If file is null.
	 */
	private ResourceListType unmarshal(File file) throws JAXBException {
		Object obj = unmar.unmarshal(file);
		if (obj instanceof JAXBElement<?>) {
			JAXBElement<?> element = (JAXBElement<?>) obj;
			if (element.getValue() instanceof ResourceListType) {
				return (ResourceListType) element.getValue();
			}
		}
		return null;
	}
}
