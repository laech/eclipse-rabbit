package rabbit.ui.internal.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import rabbit.ui.DisplayPreference;

/**
 * A page for displaying time spent working under different projects.
 */
public class ProjectPage extends ResourcePage {

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(this, true, false, false);
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider(this) {
			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
		};
	}

	@Override
	public void update(DisplayPreference p) {
		super.update(p);
		setMaxValue(getMaxProjectValue());
	}

	@Override
	public long getValue(Object o) {
		return (o instanceof IProject) ? getValueOfProject((IProject) o) : 0;
	}
}
