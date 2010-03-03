package rabbit.ui;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * A comparator for sorting the a {@link TableViewer} when the user is clicked
 * on a column.
 * </p>
 * <p>
 * To register a table column for sorting, simply call the column's
 * {@link TableColumn#addSelectionListener(TableColumnComparator)} method and
 * pass in an instance of this class. An instance of this class can be shared by
 * multiple columns of the same viewer.
 * </p>
 * <p>
 * The viewer must have a {@link ITableLabelProvider}.
 * </p>
 * 
 * @see SelectionListener
 * @see ITableLabelProvider
 * @see TreeViewer#setLabelProvider(ITableLabelProvider)
 */
public class TableLabelComparator extends ViewerComparator implements SelectionListener {

	/**
	 * One of {@link SWT#NONE}, {@link SWT#UP}, {@link SWT#DOWN}.
	 */
	private int sortDirection;
	private TableViewer viewer;
	private TableColumn selectedColumn;

	public TableLabelComparator(TableViewer parent) {
		sortDirection = SWT.NONE;
		selectedColumn = null;
		viewer = parent;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Widget item = e.widget;
		if (!(item instanceof TableColumn)) {
			return;
		}

		selectedColumn = (TableColumn) e.widget;
		Table table = selectedColumn.getParent();
		TableColumn previousColumn = table.getSortColumn();
		sortDirection = table.getSortDirection();

		if (previousColumn == selectedColumn) {
			sortDirection = (sortDirection == SWT.UP) ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(selectedColumn);
			sortDirection = SWT.UP;
			viewer.setComparator(this);
		}
		table.setSortDirection(sortDirection);
		viewer.refresh();
	}

	@Override
	public int compare(Viewer v, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		int value = doCompare(v, e1, e2);
		if (sortDirection == SWT.DOWN) {
			value *= -1;
		}
		return value;
	}

	protected int doCompare(Viewer v, Object e1, Object e2) {
		IBaseLabelProvider provider = viewer.getLabelProvider();

		String s1 = null;
		String s2 = null;
		if (provider instanceof ITableLabelProvider) {
			int index = 0;
			if (selectedColumn != null) {
				index = viewer.getTable().indexOf(selectedColumn);
			}
			s1 = ((ITableLabelProvider) provider).getColumnText(e1, index);
			s2 = ((ITableLabelProvider) provider).getColumnText(e2, index);
		}

		int value = 0;
		if (s1 != null && s2 != null) {
			value = s1.compareToIgnoreCase(s2);
		} else {
			value = (s1 == null) ? -1 : 1;
		}
		return value;
	}

	/**
	 * Gets the currently selected column.
	 * 
	 * @return The selected column.
	 */
	public TableColumn getSelectedColumn() {
		return selectedColumn;
	}

}
