package rabbit.core.internal.storage.xml;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import rabbit.core.RabbitCore;
import rabbit.core.internal.storage.xml.schema.resources.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.resources.ResourceListType;
import rabbit.core.internal.storage.xml.schema.resources.ResourceType;
import rabbit.core.storage.IResourceManager;

/**
 * An XML {@link IResourceManager}.
 */
public enum XmlResourceManager implements IResourceManager, IResourceChangeListener, IWorkbenchListener {

	INSTANCE;

	private JAXBContext jaxb;
	private Unmarshaller unmar;
	private Marshaller mar;

	private ObjectFactory objectFactory;
	private Set<String> allIds;
	private Random random;

	private Map<String, Set<String>> resources;

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

	/** Constructor. */
	private XmlResourceManager() {
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
		allIds = new HashSet<String>(resources.size() * 2);
		for (Set<String> ids : resources.values()) {
			allIds.addAll(ids);
		}
	}

	private ResourceListType getData() {
		ResourceListType database = null;
		try {
			if (getDataFile().exists()) {
				database = unmarshal(ResourceListType.class, getDataFile());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (database == null) {
				database = objectFactory.createResourceListType();
			}
		}
		return database;
	}

	/**
	 * Generates a random id.
	 * 
	 * @return A random id.
	 */
	private String generateId() {
		return System.currentTimeMillis() + "" + random.nextInt();
	}

	private File getDataFile() {
		IPath path = RabbitCore.getDefault().getStoragePath()
				.append("ResourceDB").append("Resources").addFileExtension("xml");

		File file = path.toFile();
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

	@Override
	public String getPath(String id) {
		if (!allIds.contains(id)) {
			return null;
		}
		for (Map.Entry<String, Set<String>> entry : resources.entrySet()) {
			for (String str : entry.getValue()) {
				if (str.equals(id)) {
					return entry.getKey();
				}
			}
		}
		throw new AssertionFailedException("Bug?");
	}

	@Override
	public String getId(String path) {
		Set<String> ids = resources.get(path);
		if (ids != null) {
			return ids.iterator().next();
		}
		return null;
	}

	@Override
	public String insert(String path) {
		String id = getId(path);
		if (id == null) {
			id = generateId();
			while (allIds.contains(id)) {
				id = generateId();
			}
			Set<String> ids = resources.get(path.toString());
			if (ids == null) {
				ids = new HashSet<String>();
				resources.put(path.toString(), ids);
			}
			ids.add(id);
			allIds.add(id);
		}
		return id;
	}

	private void marshal(JAXBElement<?> e, File f) throws JAXBException {
		mar.marshal(e, f);
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

	private <T> T unmarshal(Class<T> type, File f) throws JAXBException {
		@SuppressWarnings("unchecked")
		JAXBElement<T> doc = (JAXBElement<T>) unmar.unmarshal(f);
		return doc.getValue();
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		write();
	}

	/**
	 * Saves the current data to disk.
	 */
	public synchronized void write() {
		// Note: the file database is shared across workspaces, so this method
		// needs to be synchronized and data need to be merged.

		ResourceListType oldData = getData();
		for (ResourceType type : oldData.getResource()) {

			Set<String> ids = resources.get(type.getPath());
			if (ids == null) {
				ids = new HashSet<String>();
				resources.put(type.getPath(), ids);
			}
			ids.addAll(type.getResourceId());
			allIds.addAll(type.getResourceId());
		}
		try {
			marshal(objectFactory.createResources(convert(resources)), getDataFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		Map<String, Set<String>> data = new HashMap<String, Set<String>>(v.getResource().size() * 2);
		for (ResourceType type : v.getResource()) {
			Set<String> ids = new HashSet<String>(type.getResourceId().size() * 2);
			ids.addAll(type.getResourceId());
			data.put(type.getPath(), ids);
		}
		return data;
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}
}
