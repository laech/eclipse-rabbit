package rabbit.ui.internal.pages;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import rabbit.ui.IPage;
import rabbit.ui.IValueProvider;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.TreeLabelComparator;

public abstract class AbstractValueProviderPage implements IValueProvider, IPage {

	private long maxValue = 0;
	
	public AbstractValueProviderPage() {
	}

	@Override
	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long value) {
		maxValue = value;
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		return new IContributionItem[0];
	}

	@Override
	public Image getImage() {
		return null;
	}
	
	protected TableLabelComparator createValueSorterForTable(TableViewer viewer) {
		return new TableLabelComparator(viewer) {
			@Override
			protected int doCompare(Viewer v, Object e1, Object e2) {
				return (getValue(e1) > getValue(e2)) ? 1 : -1;
			}
		};
	}

	protected TreeLabelComparator createValueSorterForTree(TreeViewer viewer) {
		return new TreeLabelComparator(viewer) {
			@Override
			protected int doCompare(Viewer v, Object e1, Object e2) {
				return (getValue(e1) > getValue(e2)) ? 1 : -1;
			}
		};
	}
}
