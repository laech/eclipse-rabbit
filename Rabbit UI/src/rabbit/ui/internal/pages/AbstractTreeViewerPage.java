package rabbit.ui.internal.pages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.ui.CellPaintProvider;
import rabbit.ui.internal.RabbitUI;

public abstract class AbstractTreeViewerPage extends AbstractValueProviderPage {

	private TreeViewer viewer;
	private TreeViewerColumn graphCol;

	public AbstractTreeViewerPage() {
	}

	@Override
	public void createContents(Composite parent) {
		viewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				saveState();
			}
		});

		createColumns(viewer);

		// Special column for painting:
		graphCol = new TreeViewerColumn(viewer, SWT.LEFT);
		graphCol.setLabelProvider(new CellPaintProvider(this));
		graphCol.getColumn().setWidth(100);
		graphCol.getColumn().addSelectionListener(createValueSorterForTree(viewer));

		for (TreeColumn column : viewer.getTree().getColumns()) {
			column.setMoveable(true);
			column.setResizable(true);
		}
		viewer.setComparator(createComparator(viewer));
		restoreState();
	}

	/** Saves the state of the page. */
	protected void saveState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TreeColumn column : getViewer().getTree().getColumns()) {
			store.setValue(getWidthPreferenceString(column), column.getWidth());
		}
	}

	/** Restores the state of the page. */
	protected void restoreState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TreeColumn column : getViewer().getTree().getColumns()) {
			int width = store.getInt(getWidthPreferenceString(column));
			if (width > 0) {
				column.setWidth(width);
			}
		}
	}

	private String getWidthPreferenceString(TreeColumn column) {
		return getClass().getSimpleName() + '.' + column.getText() + "Width";
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public int getColumnWidth() {
		return graphCol.getColumn().getWidth();
	}

	protected abstract ViewerComparator createComparator(TreeViewer viewer);

	protected abstract void createColumns(TreeViewer viewer);

	protected abstract ITableLabelProvider createLabelProvider();

	protected abstract IContentProvider createContentProvider();

}
