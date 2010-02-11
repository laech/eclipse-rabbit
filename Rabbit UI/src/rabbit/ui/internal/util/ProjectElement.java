package rabbit.ui.internal.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * Represents a project resource.
 */
public class ProjectElement extends ResourceElement {

	private IPath path;
	private String name;
	private boolean isExisting;

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The full path in the workspace to this resource.
	 * @throws IllegalArgumentException
	 *             If the path is not 1 segment long.
	 * @throws NullPointerException
	 *             If the path is null.
	 */
	public ProjectElement(IPath path) {
		setPath(path);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPath getPath() {
		return path;
	}

	@Override
	public ResourceType getType() {
		return ResourceType.PROJECT;
	}

	@Override
	public double getValue() {
		double value = 0;
		for (ResourceElement e : getChildren()) {
			value += e.getValue();
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If path is not 1 segment long.
	 * @throws NullPointerException
	 *             If the new path is null.
	 */
	@Override
	protected void setPath(IPath path) {
		if (path == null) {
			throw new NullPointerException();
		}
		if (path.segmentCount() != 1) {
			throw new IllegalArgumentException();
		}
		this.path = path;
		name = path.segment(0);
		isExisting = ResourcesPlugin.getWorkspace().getRoot().getProject(getPath().toString()).exists();
	}

	@Override
	public boolean exists() {
		return isExisting;
	}

}
