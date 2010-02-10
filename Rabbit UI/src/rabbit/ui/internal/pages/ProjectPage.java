package rabbit.ui.internal.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.ui.internal.ResourceElement;
import rabbit.ui.internal.ResourceElement.ResourceType;

public class ProjectPage extends ResourcePage {

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new FilePageLabelProvider(this, true, false, false);
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		getViewer().addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return (element instanceof ResourceElement) && ((ResourceElement) element).getType() == ResourceType.PROJECT;
			}

		});
	}
}
