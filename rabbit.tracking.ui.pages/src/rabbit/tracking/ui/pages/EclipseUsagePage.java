package rabbit.tracking.ui.pages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.IPage;

public class EclipseUsagePage implements IPage {

	public EclipseUsagePage() {
	}

	@Override
	public void createContents(Composite parent) {
	}

	@Override
	public ImageDescriptor getIcon() {
		return null;//PlatformUI.getWorkbench().getSharedImages().getImageDescriptor("");
	}

	@Override
	public void update(DisplayPreference preference) {
	}

}
