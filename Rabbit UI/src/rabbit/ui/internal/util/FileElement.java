package rabbit.ui.internal.util;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * Represents a file resource.
 */
public class FileElement extends ResourceElement {

	private long value;
	private boolean isExisting;
	private String name;
	private IPath path;

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The full path in the workspace to this resource.
	 * @param value
	 *            The usage value in milliseconds.
	 * @throws IllegalArgumentException
	 *             If the path has less than two segments, or the value is
	 *             negative.
	 * @throws NullPointerException
	 *             If the path is null.
	 */
	public FileElement(IPath path, long value) {
		setPath(path);
		setValue(value);
	}

	@Override
	public IPath getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If path is less than two segments.
	 * @throws NullPointerException
	 *             If the new path is null.
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
		name = path.lastSegment();
		isExisting = ResourcesPlugin.getWorkspace().getRoot().getFile(getPath()).exists();
	}

	@Override
	public ResourceType getType() {
		return ResourceType.FILE;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getValue() {
		return value;
	}

	/**
	 * Sets the usage value of this resource.
	 * 
	 * @param value
	 *            The usage value in milliseconds.
	 * @throws IllegalArgumentException
	 *             If the value is negative.
	 */
	protected void setValue(long value) {
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		this.value = value;
	}

	@Override
	public Set<ResourceElement> getChildren() {
		return Collections.emptySet();
	}

	@Override
	public boolean exists() {
		return isExisting;
	}

	@Override
	public ResourceElement insert(ResourceElement element) {
		return null;
	}
}
