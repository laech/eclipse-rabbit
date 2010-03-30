package rabbit.ui.internal.pages;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.LaunchDescriptor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.internal.SharedImages;

public class LaunchPage extends AbstractTreeViewerPage {
	
	private IAccessor<Set<LaunchDescriptor>> accessor = RabbitCore.getLaunchDataAccessor();
	
	private IAction collapseAllAction = new Action("Collapse All") {
		@Override
		public void run() {
			getViewer().collapseAll();
		}
	};

	private IAction expandAllAction = new Action("Expand All") {
		@Override
		public void run() {
			getViewer().expandAll();
		}
	};

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		expandAllAction.setImageDescriptor(SharedImages.EXPAND_ALL);
		IContributionItem expandAll = new ActionContributionItem(expandAllAction);
		toolBar.add(expandAll);

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		ImageDescriptor img = images.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
		collapseAllAction.setImageDescriptor(img);
		IContributionItem collapseAll = new ActionContributionItem(collapseAllAction);
		toolBar.add(collapseAll);

		return new IContributionItem[] { expandAll, collapseAll };
	}

	@Override
	protected void createColumns(TreeViewer viewer) {
		TreeLabelComparator textSorter = new TreeLabelComparator(viewer);
		TreeLabelComparator valueSorter = createValueSorterForTree(viewer);

		int[] widths = new int[] { 200, 80, 80, 150, 100 };
		int[] styles = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT };
		String[] names = new String[] { "Name", "Type", "Mode", "Time", "Total Duration" };

		for (int i = 0; i < names.length; i++) {
			TreeColumn column = new TreeColumn(viewer.getTree(), styles[i]);
			column.setText(names[i]);
			column.setWidth(widths[i]);
			column.addSelectionListener(
					(names.length - 1 == i) ? valueSorter : textSorter);
		}
	}

	@Override
	protected ViewerComparator createComparator(TreeViewer viewer) {
		return new ViewerComparator();
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new LaunchPageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new LaunchPageLabelProvider();
	}

	@Override
	public long getValue(Object element) {
		if (element instanceof LaunchDescriptor) {
			return ((LaunchDescriptor) element).getDuration();
		}
		return 0;
	}

	@Override
	public void update(DisplayPreference preference) {
		Set<LaunchDescriptor> data = accessor.getData(
				preference.getStartDate(), preference.getEndDate());
		
		setMaxValue(0);
		for (LaunchDescriptor des : data) {
			if (des.getDuration() > getMaxValue()) {
				setMaxValue(des.getDuration());
			}
		}
		
		Object[] elements = getViewer().getExpandedElements();
		getViewer().setInput(data);
		try {
			getViewer().setExpandedElements(elements);
		} catch (Exception e) {
			// Ignore.
		}
	}

}
