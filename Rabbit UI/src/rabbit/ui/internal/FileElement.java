package rabbit.ui.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class FileElement extends ResourceElement {

	private double value;
	private IPath path;
	private boolean isExisting;

	public FileElement(IPath path, double value) {
		setPath(path);
		setValue(value);
	}

	@Override
	public IPath getPath() {
		return path;
	}

	@Override
	protected void setPath(IPath path) {
		if (path.segmentCount() < 2) {
			throw new IllegalArgumentException();
		}
		this.path = path;
		this.isExisting = ResourcesPlugin.getWorkspace().getRoot().getFile(getPath()).exists();
	}

	@Override
	public ResourceType getType() {
		return ResourceType.FILE;
	}

	@Override
	public String getName() {
		return getPath().segment(1);
	}

	@Override
	public double getValue() {
		return value;
	}

	protected void setValue(double value) {
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
}
