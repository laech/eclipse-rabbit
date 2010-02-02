package rabbit.tracking.ui.pages;

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

public abstract class AbstractGraphTablePage implements IPage {
	

	private long maxUsage;
	
	public AbstractGraphTablePage() {
		setMaxUsage(0);
	}
	
	@Override
	public void createContents(Composite parent) {
		doCreateContents(parent);
		getViewer().getTable().addListener(SWT.EraseItem, createEraseItemListener());
		getViewer().getTable().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int width = 0;
				for (TableColumn c : getAllColumns()) {
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
	}
	
	private Listener createEraseItemListener() {
		return new Listener() {
			
			@Override
			public void handleEvent(Event e) {
				TableItem item = (TableItem) e.item;
				Table table = item.getParent();
				
				GC gc = e.gc;
				Color oldBackground = gc.getBackground();
				int oldAntialias = gc.getAntialias();
				
				int x = getX(table);
				int y = e.y;
				int height = e.height - 1;
				int width = 0;
				try {
					width = Integer.parseInt(item.getText(table.indexOf(getUsageColumn())));
					width = (int) ((double) width / getMaxUsage() * getGraphColumn().getWidth());
					width = (width == 0) ? 2 : width;
				} catch (NumberFormatException ex) {
					width = 0;
				}

				gc.setAntialias(SWT.ON);
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));
				gc.fillRectangle(x, y, 2, height);
				gc.fillRoundRectangle(x, y, width, height, 4, 4);
				
				gc.setBackground(oldBackground);
				gc.setAntialias(oldAntialias);
			}
			
			private int getX(Table table) {
				int x = 0;
				for (int i = 0; i < table.indexOf(getGraphColumn()); i++) {
					x += table.getColumn(i).getWidth();
				}
				return x;
			}
		};
	}
	
	protected long getMaxUsage() {
		return maxUsage;
	}
	
	protected void setMaxUsage(long max) {
		maxUsage = max;
	}
	
	protected abstract TableColumn[] getAllColumns();
	
	protected abstract TableViewer getViewer();

	protected abstract void doCreateContents(Composite parent);
	
	protected abstract TableColumn getUsageColumn();

	protected abstract TableColumn getGraphColumn();

}
