package rabbit.ui;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * A label provider for a viewer column that paints horizontal bars in the cells.
 */
public class CellPainter extends StyledCellLabelProvider {
	
	/**
	 * A value provider that provides values for elements.
	 */
	public interface IValueProvider {

		/**
		 * Gets the value of the given element.
		 * @param element The element.
		 * @return The value.
		 */
		long getValue(Object element);

		/**
		 * Gets the maximum value of all the elements.
		 * @return The maximum value.
		 */
		long getMaxValue();

		/**
		 * Gets the width of the column to be painted.
		 * @return The width.
		 */
		int getColumnWidth();

	}

	private Color background;
	private Color foreground;
	private IValueProvider valueProvider;
	private final boolean isLinux;

	/**
	 * Constructor.
	 * @param valueProvider The provider for getting the values of each cell from.
	 */
	public CellPainter(IValueProvider valueProvider) {
		this.valueProvider = valueProvider;
		isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
	}
	
	@Override
	public void initialize(ColumnViewer viewer, ViewerColumn column) {
		super.initialize(viewer, column);
		
		Display display = viewer.getControl().getDisplay();
		background = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		foreground = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	@Override
	public void paint(Event e, Object element) {

		int width = getWidth(element);
		if (width == 0) {
			return;
		}
		int x = e.x;
		int y = e.y + 1;
		int height = e.height - 2;

		GC gc = e.gc;
		Color oldBackground = gc.getBackground();
		Color oldForeground = gc.getForeground();
		int oldAnti = gc.getAntialias();

		gc.setBackground(background);
		if (isLinux) {
			gc.fillRectangle(x, y, width, height);
		} else {
			gc.setAntialias(SWT.ON);
			gc.fillRectangle(x, y, 2, height);
			gc.fillRoundRectangle(x, y, width, height, 4, 4);
		}
		gc.setForeground(foreground);
		gc.drawLine(x - 1, y, x - 1, y + height -1);
		gc.drawLine(x + width, y, x + width, y + height-1);

		gc.setBackground(oldBackground);
		gc.setForeground(oldForeground);
		gc.setAntialias(oldAnti);
	}

	/**
	 * Gets the width in pixels for the paint.
	 */
	private int getWidth(Object element) {
		int fullWidth = valueProvider.getColumnWidth();

		long value = valueProvider.getValue(element);
		int width = (int) (value * fullWidth / (double) valueProvider.getMaxValue());
		width = ((value != 0) && (width == 0)) ? 2 : width;

		if (value != 0 && width < 2) {
			width = 2;
		}
		return width;
	}
}
