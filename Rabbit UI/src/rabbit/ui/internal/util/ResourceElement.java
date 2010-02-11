package rabbit.ui.internal.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;

/**
 * Represents a resource with a usage value. Intended to be used internally
 * only.
 */
public abstract class ResourceElement {

	/**
	 * Types of a resource.
	 */
	public enum ResourceType {

		/** A file resource. */
		FILE,

		/** A folder resource. */
		FOLDER,

		/** A project resource. */
		PROJECT
	}

	private Set<ResourceElement> children;

	/** Constructor. */
	public ResourceElement() {
		children = new HashSet<ResourceElement>();
	}

	/**
	 * Gets the children of this resource.
	 * 
	 * @return The children resources.
	 */
	public Set<ResourceElement> getChildren() {
		return children;
	}

	/**
	 * Inserts a new resource into this resource.
	 * 
	 * @param element
	 *            The resource to be inserted.
	 * @return If an identical resource is already exists, return it after its
	 *         children has been merged with the new resource's children.
	 *         Otherwise inserts the new resource and returns the new resource
	 *         itself. Or null if this resource does not support insertion.
	 */
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

	@Override
	public String toString() {
		return getPath().toOSString();
	}

	/**
	 * Checks whether this resource exists in the workspace.
	 * 
	 * @return True if this resource exists in the workspace, false otherwise.
	 */
	public abstract boolean exists();

	/**
	 * Gets the name of this resource.
	 * 
	 * @return The name of this resource.
	 */
	public abstract String getName();

	/**
	 * Gets the full path in the workspace to this resource.
	 * 
	 * @return The full path.
	 */
	public abstract IPath getPath();

	/**
	 * Gets the type of this resource.
	 * 
	 * @return The type of this resource.
	 */
	public abstract ResourceType getType();

	/**
	 * Gets the usage value of this resource.
	 * 
	 * @return The usage value.
	 */
	public abstract double getValue();

	/**
	 * Sets the path to this resource.
	 * 
	 * @param path
	 *            The new path.
	 * @throws IllegalArgumentException
	 *             If the new path is not valid for this resource type.
	 * @throws NullPointerException
	 *             If the new path is null.
	 */
	protected abstract void setPath(IPath path);
}
