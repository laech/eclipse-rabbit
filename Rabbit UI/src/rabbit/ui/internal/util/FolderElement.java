package rabbit.ui.internal.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.PlatformUI;

/**
 * Represents a folder resource.
 */
public class FolderElement extends ResourceElement {

	private IPath path;
	private String name;
	private boolean isExisting;

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The full path in the workspace to this resource.
	 * @throws IllegalArgumentException
	 *             If the path is less than 2 segments long.
	 * @throws NullPointerException
	 *             If the path is null.
	 */
	public FolderElement(IPath path) {
		setPath(path);
		PlatformUI.getWorkbench().getSharedImages();
	}

	@Override
	public IPath getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If path is less than 2 segments long.
	 * @throws NullPointerException
	 *             If the path is null.
	 */
	@Override
	protected void setPath(IPath path) {
		if (path == null) {
			throw new NullPointerException();
		}
		if (path.segmentCount() < 2) {
			throw new IllegalArgumentException();
		}
		this.path = path;
		name = path.removeFirstSegments(1).toString().replace('/', '.');
		isExisting = ResourcesPlugin.getWorkspace().getRoot().getFolder(getPath()).exists();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceType getType() {
		return ResourceType.FOLDER;
	}

	@Override
	public double getValue() {
		double value = 0;
		for (ResourceElement e : getChildren()) {
			value += e.getValue();
		}
		return value;
	}

	@Override
	public boolean exists() {
		return isExisting;
	}
}
