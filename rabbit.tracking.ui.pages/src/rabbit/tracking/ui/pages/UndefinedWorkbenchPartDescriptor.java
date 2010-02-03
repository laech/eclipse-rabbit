package rabbit.tracking.ui.pages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartDescriptor;

public class UndefinedWorkbenchPartDescriptor implements IWorkbenchPartDescriptor {

	private String id;
	
	public UndefinedWorkbenchPartDescriptor(String id) {
		this.id = id;
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
		return null;
	}

}
