package rabbit.ui.internal.pages;

import java.util.Collection;

/**
 * Collection provider takes a collection of inputs.
 */
public class CollectionContentProvider extends AbstractTreeContentProvider {

	@Override
	public Object[] getElements(Object input) {
		return getChildren(input);
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
