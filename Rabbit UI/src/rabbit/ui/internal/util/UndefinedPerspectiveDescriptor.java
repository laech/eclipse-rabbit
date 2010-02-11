package rabbit.ui.internal.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;

/**
 * Represents an undefined perspective.
 */
public class UndefinedPerspectiveDescriptor implements IPerspectiveDescriptor {

	private String id;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            The id of this perspective.
	 */
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
