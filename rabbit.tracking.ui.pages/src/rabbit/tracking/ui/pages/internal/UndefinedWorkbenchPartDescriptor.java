package rabbit.tracking.ui.pages.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartDescriptor;

/**
 * Represents an undefined {@link IWorkbenchPartDescriptor}. The descriptor may
 * have been available before but no longer available now.
 */
public class UndefinedWorkbenchPartDescriptor implements IWorkbenchPartDescriptor {

	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id The id of the descriptor.
	 */
	public UndefinedWorkbenchPartDescriptor(String id) {
		this.id = id;
	}

	@Override public String getId() {
		return id;
	}

	@Override public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override public String getLabel() {
		return null;
	}

}
