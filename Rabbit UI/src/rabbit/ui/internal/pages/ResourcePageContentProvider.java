package rabbit.ui.internal.pages;

import java.util.Collection;

import rabbit.ui.internal.util.ResourceElement;

public class ResourcePageContentProvider extends AbstractTreeContentProvider {

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof Collection<?>) {
			return ((Collection<?>) parent).toArray();
		} else if (parent instanceof ResourceElement) {
			return ((ResourceElement) parent).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof ResourceElement)
				&& !((ResourceElement) element).getChildren().isEmpty();
	}
}