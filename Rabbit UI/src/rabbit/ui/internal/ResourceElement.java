package rabbit.ui.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.Path;

public class ResourceElement {

	public enum ResourceType {
		FILE, FOLDER, PROJECT
	}

	public static ResourceElement createFile(String fullPath, double value) {
		return new ResourceElement(fullPath, ResourceType.FILE, value);
	}

	public static ResourceElement createFolder(String fullPath) {
		return new ResourceElement(fullPath, ResourceType.FOLDER, 0);
	}

	public static ResourceElement createProject(String fullPath) {
		return new ResourceElement(fullPath, ResourceType.PROJECT, 0);
	}

	public static String getFileName(String filePath) {
		try {
			return filePath.substring(filePath.lastIndexOf('/') + 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String getFolderPath(String filePath) {
		try {
			if (filePath.indexOf('/', 1) == filePath.lastIndexOf('/')) {
				return null;
			}
			return filePath.substring(0, filePath.lastIndexOf('/'));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public static String getProjectPath(String filePath) {
		try {
			return filePath.substring(0, filePath.indexOf('/', 1));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	private Set<ResourceElement> children;
	private ResourceType type;
	private String fullPath;
	private double value;

	private boolean exists;
	IWorkspaceRoot workspace;// = ResourcesPlugin.getWorkspace().getRoot();

	private ResourceElement(String path, ResourceType type, double value) {
		this.fullPath = path;
		this.type = type;
		this.value = value;
		children = new HashSet<ResourceElement>();
		workspace = ResourcesPlugin.getWorkspace().getRoot();
		try {
			switch (type) {
			case PROJECT:
				exists = workspace.getProject(getPath()).exists();
			case FOLDER:
				exists = workspace.getFolder(Path.fromPortableString(getPath())).exists();
			case FILE:
				exists = workspace.getFile(Path.fromPortableString(getPath())).exists();
			default:
				exists = true;
			}
		} catch (Exception e) {
			exists = true;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		return ((ResourceElement) obj).getPath().equals(getPath());
	}

	public Set<ResourceElement> getChildren() {
		return children;
	}

	public String getName() {
		try {
			switch (getType()) {
			case PROJECT:
				return getPath().substring(1);
			case FOLDER:
				return getPath().substring(getPath().indexOf('/', 1) + 1).replace("/", ".");
			case FILE:
				return getPath().substring(getPath().lastIndexOf("/") + 1);
			default:
				throw new AssertionFailedException("Unknown type");
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public boolean exists() {
		return exists;
	}

	public String getPath() {
		return fullPath;
	}

	public ResourceType getType() {
		return type;
	}

	public double getValue() {
		switch (getType()) {
		case FILE:
			return value;
		default:
			value = 0;
			for (ResourceElement element : getChildren()) {
				value += element.getValue();
			}
			return value;
		}
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	public ResourceElement insert(String path, ResourceType type, double value) {
		for (ResourceElement l : children) {
			if (l.getPath().equals(path) && l.getType() == type) {
				return l;
			}
		}
		ResourceElement l = new ResourceElement(path, type, value);
		children.add(l);
		return l;
	}

	@Override
	public String toString() {
		return getPath();
	}
}
