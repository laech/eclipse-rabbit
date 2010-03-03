package rabbit.ui;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;


public class CellPaintProvider extends StyledCellLabelProvider {

	private IValueProvider valueProvider;

	public CellPaintProvider(IValueProvider valueProvider) {
		this.valueProvider = valueProvider;
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

		gc.setBackground(e.display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		//			if (isLinux) {
		//				gc.fillRectangle(x, y, width, height);
		//			} else {
		gc.setAntialias(SWT.ON);
		gc.fillRectangle(x, y, 2, height);
		gc.fillRoundRectangle(x, y, width, height, 4, 4);
		//			}
		gc.setForeground(e.display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		gc.drawLine(x - 1, y - 1, x - 1, y + height);
		gc.drawLine(x + width, y - 1, x + width, y + height);

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
		} else if (width == fullWidth) {
			width -= 1;
		}
		return width;
	}
}
