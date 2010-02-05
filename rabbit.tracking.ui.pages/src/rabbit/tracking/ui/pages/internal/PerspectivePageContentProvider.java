package rabbit.tracking.ui.pages.internal;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Label provider for {@linkPerspectivePage}
 */
public class PerspectivePageContentProvider implements IStructuredContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	@Override public Object[] getElements(Object input) {
		if (input instanceof Collection<?>)
			return ((Collection<?>) input).toArray();
		else
			return EMPTY_ARRAY;
	}

	@Override public void dispose() {}

	@Override public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

}
