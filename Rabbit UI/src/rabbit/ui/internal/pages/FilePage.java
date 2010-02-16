package rabbit.ui.internal.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.internal.util.FileElement;

/**
 * A page for displaying time spent working on different files.
 */
public class FilePage extends ResourcePage {

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(this, false, false, true);
	}

	@Override
	long getValue(Object o) {
		if (!(o instanceof FileElement)) {
			return 0;
		}
		return super.getValue(o);
	}
}
