package rabbit.ui.internal.pages;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.DisplayPreference;

/**
 * A page for displaying time spent working on different files.
 */
public class FilePage extends ResourcePage {

	public FilePage() {
		super();
	}

	@Override
	public void update(DisplayPreference p) {
		super.update(p);
		setMaxValue(getMaxFileValue());
	}

	@Override
	public long getValue(Object o) {
		return (o instanceof IFile) ? getValueOfFile((IFile) o) : 0;
	}

	@Override
	protected String getValueColumnText() {
		return "Time Spent";
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}
}
