package rabbit.tracking.ui.pages;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import rabbit.tracking.ui.IPage;

/**
 * A page that contains a table that is capable of painting graphics.
 * <p>
 * The table has a column containing numerical values, and a column used for
 * painting color bars. The length of the bar is determined by
 * {@link #getMaxValue()}, when the table viewer's data is changed, call
 * {@link #setMaxValue(double)} to indicated the new maximum value.
 * </p>
 * <p>
 * By default, the value column and the graph column is constructed after all
 * other columns, that means the index of the graph column is the last index,
 * and the index of the value column is last index - 1.
 * </p>
 */
public abstract class AbstractGraphTablePage implements IPage {

	private double maxValue;

	private TableViewer viewer;
	private TableColumn valueColumn;
	private TableColumn graphColumn;

	private Listener tablePainter = new Listener() {
		@Override public void handleEvent(Event e) {

			TableItem item = (TableItem) e.item;
			Table table = item.getParent();

			int width = getWidth(table, item);
			if (width == 0) {
				return;
			}
			int x = getX(table);
			int y = e.y + 1;
			int height = e.height - 1;

			GC gc = e.gc;
			Color oldBackground = gc.getBackground();
			int oldAntialias = gc.getAntialias();

			gc.setAntialias(SWT.ON);
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
			gc.fillRectangle(x, y, 2, height);
			gc.fillRoundRectangle(x, y, width, height, 4, 4);

			gc.setBackground(oldBackground);
			gc.setAntialias(oldAntialias);
		}

		/**
		 * Gets the x position for painting.
		 * 
		 * @param table The table to be painted.
		 */
		private int getX(Table table) {
			int x = 0;
			for (int i : table.getColumnOrder()) {

				TableColumn column = table.getColumn(i);
				if (column != getGraphColumn()) {
					x += column.getWidth();
				} else {
					break;
				}
			}
			return x;
		}

		/**
		 * Gets the width in pixels for the paint.
		 * 
		 * @param table The table to be painted.
		 * @param item The table item to be painted.
		 * @return The width in pixels.
		 */
		private int getWidth(Table table, TableItem item) {
			int width = 0;
			try {
				double value = Double.parseDouble(item.getText(table.indexOf(getValueColumn())));
				width = (int) (value * getGraphColumn().getWidth() / getMaxValue());
				width = ((value != 0) && (width == 0)) ? 2 : width;
			} catch (NumberFormatException ex) {
			}
			return width;
		}
	};

	/**
	 * Constructor.
	 */
	public AbstractGraphTablePage() {
		setMaxValue(0);
	}

	@Override public void createContents(Composite parent) {
		viewer = new TableViewer(parent, SWT.HIDE_SELECTION);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.addListener(SWT.EraseItem, tablePainter);

		TableColumnComparator sorter = new TableColumnComparator(viewer);
		for (TableColumn column : createColumns(table)) {
			column.addSelectionListener(sorter);
		}

		valueColumn = new TableColumn(table, SWT.RIGHT);
		valueColumn.setText(getValueColumnText());
		valueColumn.setWidth(100);
		valueColumn.setMoveable(true);
		valueColumn.addSelectionListener(sorter);

		graphColumn = new TableColumn(table, SWT.LEFT);
		graphColumn.setWidth(100);
		graphColumn.setMoveable(true);
		graphColumn.addSelectionListener(new TableColumnComparator(viewer) {
			@Override protected int getSelectedColumnIndex() {
				return table.indexOf(getValueColumn());
			}
		});
	}

	/**
	 * Gets the current maximum value used for painting.
	 * 
	 * @return The maximum.
	 */
	protected double getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets the maximum value for painting.
	 * 
	 * @param max The value.
	 * @see #getGraphColumn()
	 */
	protected void setMaxValue(double max) {
		maxValue = max;
	}

	/**
	 * Gets the column that is used for painting.
	 * 
	 * @return The column that is used for painting.
	 */
	protected TableColumn getGraphColumn() {
		return graphColumn;
	}

	/**
	 * Gets the table column for displaying usage information.
	 * 
	 * @return The table column for displaying the usage information.
	 */
	protected TableColumn getValueColumn() {
		return valueColumn;
	}

	/**
	 * Gets the table viewer of this page.
	 * 
	 * @return The table viewer.
	 */
	protected TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Creates an content provider for the table viewer.
	 * 
	 * @return An content provider.
	 * @see #getViewer()
	 */
	protected abstract IStructuredContentProvider createContentProvider();

	/**
	 * Creates an label provider for the table viewer.
	 * 
	 * @return An label provider.
	 * @see #getViewer()
	 */
	protected abstract ITableLabelProvider createLabelProvider();

	/**
	 * Creates the additional columns for the table.
	 * 
	 * @param table The table to create columns for.
	 * @return The created columns.
	 */
	protected abstract TableColumn[] createColumns(Table table);

	/**
	 * Gets the title text for the table column displaying the usage.
	 * 
	 * @return The title text.
	 */
	protected abstract String getValueColumnText();
}
