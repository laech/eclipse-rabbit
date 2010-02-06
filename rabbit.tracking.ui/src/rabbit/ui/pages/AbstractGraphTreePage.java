package rabbit.ui.pages;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A page that contains a tree (can be used as a table) that is capable of
 * painting graphics.
 * <p>
 * The tree has a column containing numerical values, and a column used for
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
public abstract class AbstractGraphTreePage implements IPage {

	private double maxValue;

	private TreeViewer viewer;
	private TreeColumn valueColumn;
	private TreeColumn graphColumn;

	private Listener graphPainter = new Listener() {
		@Override
		public void handleEvent(Event e) {

			TreeItem item = (TreeItem) e.item;
			Tree tree = item.getParent();

			int width = getWidth(tree, item);
			if (width == 0) {
				return;
			}
			int x = getX(tree);
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
		 * @param tree
		 *            The tree to be painted.
		 */
		private int getX(Tree tree) {
			int x = 0;
			for (int i : tree.getColumnOrder()) {

				TreeColumn column = tree.getColumn(i);
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
		 * @param tree
		 *            The tree to be painted.
		 * @param item
		 *            The tree item to be painted.
		 * @return The width in pixels.
		 */
		private int getWidth(Tree tree, TreeItem item) {
			int width = 0;
			try {
				double value = Double.parseDouble(item.getText(tree.indexOf(getValueColumn())));
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
	public AbstractGraphTreePage() {
		setMaxValue(0);
	}

	@Override
	public void createContents(Composite parent) {
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());

		final Tree Tree = viewer.getTree();
		Tree.setHeaderVisible(true);
		Tree.addListener(SWT.PaintItem, graphPainter);

		ColumnComparator sorter = new ColumnComparator(viewer);
		for (TreeColumn column : createColumns(Tree)) {
			column.addSelectionListener(sorter);
		}

		valueColumn = new TreeColumn(Tree, SWT.RIGHT);
		valueColumn.setText(getValueColumnText());
		valueColumn.setWidth(100);
		valueColumn.setMoveable(true);
		valueColumn.addSelectionListener(sorter);

		graphColumn = new TreeColumn(Tree, SWT.LEFT);
		graphColumn.setWidth(100);
		graphColumn.setMoveable(true);
		graphColumn.addSelectionListener(new ColumnComparator(viewer) {
			@Override
			protected int getSelectedColumnIndex() {
				return Tree.indexOf(getValueColumn());
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
	 * @param max
	 *            The value.
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
	protected TreeColumn getGraphColumn() {
		return graphColumn;
	}

	/**
	 * Gets the column for displaying usage information.
	 * 
	 * @return The column for displaying the usage information.
	 */
	protected TreeColumn getValueColumn() {
		return valueColumn;
	}

	/**
	 * Gets the viewer of this page.
	 * 
	 * @return The viewer.
	 */
	protected TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Creates an content provider for the viewer.
	 * 
	 * @return An content provider.
	 * @see #getViewer()
	 */
	protected abstract ITreeContentProvider createContentProvider();

	/**
	 * Creates an label provider for the viewer.
	 * 
	 * @return An label provider.
	 * @see #getViewer()
	 */
	protected abstract ITableLabelProvider createLabelProvider();

	/**
	 * Creates the additional columns for the tree.
	 * 
	 * @param tree
	 *            The tree to create columns for.
	 * @return The created columns.
	 */
	protected abstract TreeColumn[] createColumns(Tree tree);

	/**
	 * Gets the title text for the Tree column displaying the usage.
	 * 
	 * @return The title text.
	 */
	protected abstract String getValueColumnText();
}
