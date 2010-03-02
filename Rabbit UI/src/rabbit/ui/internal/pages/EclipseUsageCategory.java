package rabbit.ui.internal.pages;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.DisplayPreference;

/**
 * An empty category page used to contain other pages.
 */
public class EclipseUsageCategory extends AbstractPage {

	public EclipseUsageCategory() {
	}

	@Override
	public void createContents(Composite parent) {
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	@Override
	public void update(DisplayPreference preference) {
	}

}
