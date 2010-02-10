package rabbit.ui.internal;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class ProjectElement extends ResourceElement {

	private IPath path;
	private boolean isExisting;

	public ProjectElement(IPath path) {
		setPath(path);
	}

	@Override
	public String getName() {
		return getPath().segment(0);
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

	@Override
	protected void setPath(IPath path) {
		if (path.segmentCount() != 1) {
			throw new IllegalArgumentException();
		}
		this.path = path;
		this.isExisting = ResourcesPlugin.getWorkspace().getRoot().getProject(getPath().toString()).exists();
	}

	@Override
	public boolean exists() {
		return isExisting;
	}

}
