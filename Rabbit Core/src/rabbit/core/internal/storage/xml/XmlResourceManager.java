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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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

			if ((delta.getKind() != IResourceDelta.ADDED) || !(delta.getResource() instanceof IFile)) {
				return true;
			}

			IFile file = (IFile) delta.getResource();
			String newPath = file.getFullPath().toString();
			if (delta.getMovedFromPath() != null) {
				String oldPath = delta.getMovedFromPath().toString();

				Set<String> oldIds = resources.get(oldPath);
				if (oldIds == null) {
					return false;
				}

				Set<String> newIds = resources.get(newPath);
				if (newIds == null) {
					newIds = new HashSet<String>();
					resources.put(newPath, newIds);
				}
				newIds.addAll(oldIds);

				resources.remove(oldPath);
			}
			return false;
		}
	};

	/** Constructor. */
	XmlResourceManager() {
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

		resources = new HashMap<String, Set<String>>();
		objectFactory = new ObjectFactory();
		random = new Random();
		allIds = new HashSet<String>();
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

		for (ResourceType type : database.getResource()) {

			Set<String> ids = new HashSet<String>();
			ids.addAll(type.getResourceId());
			resources.put(type.getPath(), ids);
			allIds.addAll(ids);
		}
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
		IPath path = Path.fromOSString(RabbitCore.getDefault().getStoragePath().toOSString());
		path = path.addTrailingSeparator();
		path = path.append("ResourceDB");
		path = path.addTrailingSeparator();
		path = path.append("Resources");
		path = path.addFileExtension("xml");

		File file = path.toFile();
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}

	@Override
	public String getFilePath(String id) {
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
		throw new AssertionFailedException("Bug");
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
		ResourceListType database = objectFactory.createResourceListType();
		for (Map.Entry<String, Set<String>> entry : resources.entrySet()) {
			ResourceType type = objectFactory.createResourceType();
			type.setPath(entry.getKey());
			type.getResourceId().addAll(entry.getValue());
			database.getResource().add(type);
		}
		try {
			marshal(objectFactory.createResources(database), getDataFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}
}
