package rabbit.tracking.ui.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableColumnComparator extends ViewerComparator implements SelectionListener {

	public int sortDirection;
	public TableViewer viewer;

	public TableColumnComparator(TableViewer viewer) {
		sortDirection = SWT.NONE;
		this.viewer = viewer;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		TableColumn curColumn = (TableColumn) e.widget;
		Table table = curColumn.getParent();
		TableColumn preColumn = table.getSortColumn();
		sortDirection = table.getSortDirection();
		if (preColumn == curColumn) {
			sortDirection = (sortDirection == SWT.UP) ? SWT.DOWN : SWT.UP;
			viewer.refresh();
		} else {
			table.setSortColumn(curColumn);
			sortDirection = SWT.UP;
			viewer.setComparator(this);
		}
		table.setSortDirection(sortDirection);
	}

	@Override
	public int compare(Viewer v, Object e1, Object e2) {
		ITableLabelProvider lp = (ITableLabelProvider) viewer.getLabelProvider();
		String s1 = lp.getColumnText(e1, getColumnIndex());
		String s2 = lp.getColumnText(e2, getColumnIndex());

		int value = 0;
		if (s1 != null && s2 != null) {
			value = s1.compareTo(s2);
		} else {
			value = (s1 == null) ? -1 : 1;
		}

		if (sortDirection == SWT.DOWN) {
			value *= -1;
		}
		return value; 
	}

	protected abstract int getColumnIndex();
}