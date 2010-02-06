package rabbit.ui.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * <p>
 * A comparator for sorting the a {@link TableViewer} when the user is clicked
 * on a column.
 * </p>
 * <p>
 * To register a table column for sorting, simply call the column's
 * {@link TreeColumn#addSelectionListener(TableColumnComparator)} method and
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
public class ColumnComparator extends ViewerComparator implements SelectionListener {

	/**
	 * One of {@link SWT#NONE}, {@link SWT#UP}, {@link SWT#DOWN}.
	 */
	private int sortDirection;
	private TreeViewer viewer;
	private TreeColumn selectedColumn;

	public ColumnComparator(TreeViewer parent) {
		sortDirection = SWT.NONE;
		selectedColumn = null;
		viewer = parent;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		selectedColumn = (TreeColumn) e.widget;
		Tree table = selectedColumn.getParent();
		TreeColumn previousColumn = table.getSortColumn();
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
		ITableLabelProvider lp = (ITableLabelProvider) viewer.getLabelProvider();
		String s1 = lp.getColumnText(e1, getSelectedColumnIndex());
		String s2 = lp.getColumnText(e2, getSelectedColumnIndex());

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

	/**
	 * Gets the index of the currently selected column.
	 * 
	 * @return The index of the currently selected column.
	 */
	protected int getSelectedColumnIndex() {
		return viewer.getTree().indexOf(selectedColumn);
	}
}
