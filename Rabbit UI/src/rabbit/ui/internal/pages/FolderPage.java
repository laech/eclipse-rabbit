package rabbit.ui.internal.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ResourceElement;
import rabbit.ui.internal.util.ResourceElement.ResourceType;

/**
 * A page for displaying time spent working under different folders.
 */
public class FolderPage extends ResourcePage {

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		getViewer().addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return (element instanceof ResourceElement) && ((ResourceElement) element).getType() != ResourceType.FILE;
			}
		});
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(this, false, true, false);
	}

	@Override
	long getValue(Object o) {
		if (!(o instanceof FolderElement)) {
			return 0;
		}
		return super.getValue(o);
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider() {
			@Override
			public boolean hasChildren(Object o) {
				return (o instanceof ResourceElement)
						&& ((ResourceElement) o).getType() == ResourceType.PROJECT
						&& !((ResourceElement) o).getChildren().isEmpty();
			}
		};
	}
}
