package rabbit.ui.internal.pages;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.ui.DisplayPreference;

public class ResourcesCategory extends AbstractPage {

	public ResourcesCategory() {
	}

	@Override
	public void createContents(Composite parent) {
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
	}

	@Override
	public void update(DisplayPreference preference) {
	}

}
