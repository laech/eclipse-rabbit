package rabbit.ui.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;

public abstract class ResourceElement {

	public enum ResourceType {
		FILE, FOLDER, PROJECT
	}

	// public static ResourceElement createFile(String fullPath, double value) {
	// return new ResourceElement(fullPath, ResourceType.FILE, value);
	// }
	//
	// public static ResourceElement createFolder(String fullPath) {
	// return new ResourceElement(fullPath, ResourceType.FOLDER, 0);
	// }
	//
	// public static ResourceElement createProject(String fullPath) {
	// return new ResourceElement(fullPath, ResourceType.PROJECT, 0);
	// }
	//
	// public static String getFileName(String filePath) {
	// try {
	// return filePath.substring(filePath.lastIndexOf('/') + 1);
	// } catch (IndexOutOfBoundsException e) {
	// return null;
	// }
	// }
	//
	// public static String getFolderPath(String filePath) {
	// try {
	// if (filePath.indexOf('/', 1) == filePath.lastIndexOf('/')) {
	// return null;
	// }
	// return filePath.substring(0, filePath.lastIndexOf('/'));
	// } catch (IndexOutOfBoundsException e) {
	// return null;
	// }
	// }
	//
	// public static String getProjectPath(String filePath) {
	// try {
	// return filePath.substring(0, filePath.indexOf('/', 1));
	// } catch (IndexOutOfBoundsException e) {
	// return null;
	// }
	// }

	private Set<ResourceElement> children;

	public ResourceElement() {
		children = new HashSet<ResourceElement>();
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

	public abstract String getName();

	public abstract IPath getPath();

	public abstract ResourceType getType();

	public abstract double getValue();

	protected abstract void setPath(IPath path);

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	public ResourceElement insert(ResourceElement element) {
		for (ResourceElement l : children) {
			if (l.getPath().equals(element.getPath()) && l.getType() == element.getType()) {
				l.getChildren().addAll(element.getChildren());
				return l;
			}
		}
		children.add(element);
		return element;
	}

	public abstract boolean exists();

	@Override
	public String toString() {
		return getPath().toOSString();
	}
}
