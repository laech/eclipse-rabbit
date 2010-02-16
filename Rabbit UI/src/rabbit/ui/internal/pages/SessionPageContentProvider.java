package rabbit.ui.internal.pages;

import java.util.Collection;

/**
 * Content provider for a session page.
 */
public class SessionPageContentProvider extends AbstractTreeContentProvider {

	public SessionPageContentProvider() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Collection<?>) {
			return ((Collection<?>) parentElement).toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof Collection<?>);
	}

}
