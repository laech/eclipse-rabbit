package rabbit.ui.internal.pages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import rabbit.ui.CellPaintProvider;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.internal.RabbitUI;

public abstract class AbstractTableViewerPage extends AbstractValueProviderPage {

	private TableViewer viewer;
	private TableViewerColumn graphCol;

	public AbstractTableViewerPage() {
	}

	@Override
	public void createContents(Composite parent) {
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				saveState();
			}
		});

		createColumns(viewer);

		// Special column for painting:
		graphCol = new TableViewerColumn(viewer, SWT.LEFT);
		graphCol.setLabelProvider(new CellPaintProvider(this));
		graphCol.getColumn().setWidth(100);
		graphCol.getColumn().addSelectionListener(createValueSorterForTable(viewer));

		for (TableColumn column : viewer.getTable().getColumns()) {
			column.setMoveable(true);
			column.setResizable(true);
		}
		viewer.setComparator(new TableLabelComparator(viewer));
		restoreState();
	}

	/** Saves the state of the page. */
	protected void saveState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TableColumn column : getViewer().getTable().getColumns()) {
			store.setValue(getWidthPreferenceString(column), column.getWidth());
		}
	}

	/** Restores the state of the page. */
	protected void restoreState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TableColumn column : getViewer().getTable().getColumns()) {
			int width = store.getInt(getWidthPreferenceString(column));
			if (width > 0) {
				column.setWidth(width);
			}
		}
	}

	private String getWidthPreferenceString(TableColumn column) {
		return getClass().getSimpleName() + '.' + column.getText() + "Width";
	}

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	public int getColumnWidth() {
		return graphCol.getColumn().getWidth();
	}

	protected abstract void createColumns(TableViewer viewer);

	protected abstract ITableLabelProvider createLabelProvider();

	protected abstract IContentProvider createContentProvider();

}
