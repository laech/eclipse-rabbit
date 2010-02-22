package rabbit.ui.internal.pages;

import java.util.Collection;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;
import rabbit.ui.internal.util.ResourceElement.ResourceType;

/**
 * A page for displaying time spent working under different projects.
 */
public class ProjectPage extends FilePage {

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(true, false, false);
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

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider() {
			@Override
			public boolean hasChildren(Object element) {
				return (element instanceof Collection<?>);
			}
		};
	}

	@Override
	public void update(DisplayPreference p) {
		super.update(p);

		setMaxValue(0);
		for (ResourceElement project : data) {
			long value = project.getValue();
			if (value > getMaxValue()) {
				setMaxValue(value);
			}
		}
	}

	@Override
	long getValue(Object o) {
		if (o instanceof ProjectElement) {
			return ((ProjectElement) o).getValue();
		} else {
			return 0;
		}
	}
}
