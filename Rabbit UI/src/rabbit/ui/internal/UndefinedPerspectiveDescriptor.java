package rabbit.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;

public class UndefinedPerspectiveDescriptor implements IPerspectiveDescriptor {

	private String id;

	public UndefinedPerspectiveDescriptor(String id) {
		this.id = id;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getLabel() {
		return id;
	}

}
