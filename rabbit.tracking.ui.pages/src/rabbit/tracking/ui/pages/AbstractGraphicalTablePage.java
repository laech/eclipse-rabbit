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

public abstract class AbstractGraphicalTablePage implements IPage {
	
	private double maxUsage;

	private TableViewer viewer;
	private TableColumn usageCol;
	private TableColumn graphCol;
	
	public AbstractGraphicalTablePage() {
		setMaxUsage(0);
	}

	@Override
	public void createContents(Composite parent) {
		viewer = new TableViewer(parent, SWT.HIDE_SELECTION);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.addListener(SWT.EraseItem, createEraseItemListener());
		table.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int width = 0;
				for (TableColumn c : table.getColumns()) {
					if (c != getGraphColumn()) {
						width += c.getWidth();
					}
				}
				width = getViewer().getTable().getSize().x - width;
				if (width >= 100) {
					getGraphColumn().setWidth(width - 5);
				}
			}
		});
		
		TableColumnComparator sorter = new TableColumnComparator(viewer);
		for (TableColumn column : createColumns(table)) {
			column.addSelectionListener(sorter);
		}
		
		usageCol = new TableColumn(table, SWT.RIGHT);
		usageCol.setText(getUsageColumnText());
		usageCol.setWidth(100);
		usageCol.setMoveable(true);
		usageCol.addSelectionListener(sorter);
		
		graphCol = new TableColumn(table, SWT.LEFT);
		graphCol.setWidth(100);
		graphCol.setMoveable(true);
		graphCol.addSelectionListener(new TableColumnComparator(viewer) {
			@Override
			protected int getColumnIndex() {
				return table.indexOf(getUsageColumn());
			}
		});
	}

	private Listener createEraseItemListener() {
		return new Listener() {
			
			@Override
			public void handleEvent(Event e) {
				TableItem item = (TableItem) e.item;
				Table table = item.getParent();

				int width = getWidth(table, item);
				if (width == 0) {
					return;
				}
				int x = getX(table);
				int y = e.y;
				int height = e.height - 1;

				GC gc = e.gc;
				Color oldBackground = gc.getBackground();
				int oldAntialias = gc.getAntialias();
				
				gc.setAntialias(SWT.ON);
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
				gc.fillRectangle(x, y, 2, height);
				gc.fillRoundRectangle(x, y, width, height, 4, 4);
				
				gc.setBackground(oldBackground);
				gc.setAntialias(oldAntialias);
			}
			
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
			
			private int getWidth(Table table, TableItem item) {
				int width = 0;
				try {
					double value = Double.parseDouble(item.getText(table.indexOf(getUsageColumn())));
					width = (int) (value / getMaxUsage() * getGraphColumn().getWidth());
					width = (width == 0) ? 2 : width;
				} catch (NumberFormatException ex) {
					width = 0;
				}
				return width;
			}
		};
	}
	
	protected double getMaxUsage() {
		return maxUsage;
	}
	
	protected void setMaxUsage(double max) {
		maxUsage = max;
	}

	protected TableColumn getGraphColumn() {
		return graphCol;
	}

	protected TableColumn getUsageColumn() {
		return usageCol;
	}

	protected TableViewer getViewer() {
		return viewer;
	}
	
	protected abstract IStructuredContentProvider createContentProvider();
	
	protected abstract ITableLabelProvider createLabelProvider();
	
	protected abstract TableColumn[] createColumns(Table table);
	
	protected abstract String getUsageColumnText();
}
