package rabbit.ui.internal.pages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.RabbitUI;

/**
 * A page for displaying time spent working under different folders.
 */
public class FolderPage extends ResourcePage {

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(this, false, true, false);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		getViewer().addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof IContainer;
			}
		});
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		ImageDescriptor icon = PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
		IContributionItem collapseAll = new ActionContributionItem(new Action("Collapse All", icon) {
			@Override
			public void run() {
				getViewer().collapseAll();
			}
		});
		toolBar.add(collapseAll);

		icon = RabbitUI.imageDescriptorFromPlugin(RabbitUI.PLUGIN_ID, "resources/expandall.gif");
		IContributionItem expandAll = new ActionContributionItem(new Action("Expand All", icon) {
			@Override
			public void run() {
				getViewer().expandAll();
			}
		});
		toolBar.add(expandAll);

		return new IContributionItem[] { collapseAll, expandAll };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider(this) {
			@Override
			public boolean hasChildren(Object o) {
				return o instanceof IProject;
			}
		};
	}

	@Override
	public void update(DisplayPreference p) {
		super.update(p);
		setMaxValue(getMaxFolderValue());
	}

	@Override
	public long getValue(Object o) {
		return (o instanceof IFolder) ? getValueOfFolder((IFolder) o) : 0;
	}

}
