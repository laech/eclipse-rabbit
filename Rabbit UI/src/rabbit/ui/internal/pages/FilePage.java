package rabbit.ui.internal.pages;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.RabbitUI;

/**
 * A page for displaying time spent working on different files.
 */
public class FilePage extends ResourcePage {

	public FilePage() {
		super();
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

		icon = RabbitUI.imageDescriptorFromPlugin("org.eclipse.debug.ui", "icons/full/elcl16/expandall.gif");
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
	public void update(DisplayPreference p) {
		Object[] elements = getViewer().getExpandedElements();
		super.update(p);
		setMaxValue(getMaxFileValue());
		getViewer().setExpandedElements(elements);
	}

	@Override
	public long getValue(Object o) {
		return (o instanceof IFile) ? getValueOfFile((IFile) o) : 0;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}
}
