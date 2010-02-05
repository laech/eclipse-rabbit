package rabbit.tracking.ui.pages.internal;

import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for a session page.
 */
public class SessionPageContentProvider implements IStructuredContentProvider {

	public SessionPageContentProvider() {}

	@Override public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Map<?, ?>) {
			return ((Map<?, ?>) inputElement).entrySet().toArray();
		}
		return new Object[0];
	}

	@Override public void dispose() {}

	@Override public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

}
