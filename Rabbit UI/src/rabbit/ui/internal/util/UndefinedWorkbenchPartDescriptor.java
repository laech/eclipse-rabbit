package rabbit.ui.internal.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * Represents an undefined {@link IWorkbenchPartDescriptor}. The descriptor may
 * have been available before but no longer available now.
 */
public class UndefinedWorkbenchPartDescriptor implements IWorkbenchPartDescriptor {

	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of the descriptor.
	 */
	public UndefinedWorkbenchPartDescriptor(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW);
	}

	@Override
	public String getLabel() {
		return id;
	}

}
