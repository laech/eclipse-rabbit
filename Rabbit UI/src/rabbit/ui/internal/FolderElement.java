package rabbit.ui.internal;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

public class FolderElement extends ResourceElement {

	private IPath path;
	private boolean isExisting;

	public FolderElement(IPath path) {
		setPath(path);
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
		this.isExisting = ResourcesPlugin.getWorkspace().getRoot().getFolder(getPath()).exists();
	}

	@Override
	public String getName() {
		return getPath().segment(1);
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
