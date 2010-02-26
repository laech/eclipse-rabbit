package rabbit.ui.internal.pages;

import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class ResourcePageContentProvider extends AbstractTreeContentProvider {

	private ResourcePage page;

	public ResourcePageContentProvider(ResourcePage parent) {
		this.page = parent;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof IProject)
			return page.getResources((IProject) parent);
		else if (parent instanceof IFolder)
			return page.getFiles((IFolder) parent);
		else
			return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		return ((Collection<?>) inputElement).toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IContainer;
	}
}