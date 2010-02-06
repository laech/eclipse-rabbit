package rabbit.ui.internal.pages;

import java.util.Map;

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
		if (parentElement instanceof Map<?, ?>) {
			return ((Map<?, ?>) parentElement).entrySet().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof Map<?, ?>);
	}

}
