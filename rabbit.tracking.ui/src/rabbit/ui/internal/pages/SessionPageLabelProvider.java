package rabbit.ui.internal.pages;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider for a session page.
 */
public class SessionPageLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	private static Format formatter = new DecimalFormat("#0.00");

	public SessionPageLabelProvider() {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof Map.Entry<?, ?>)) {
			return null;
		}

		Map.Entry<?, ?> entry = (Entry<?, ?>) element;
		switch (columnIndex) {
		case 0:
			return entry.getKey().toString();
		case 1:
			try {
				return formatter.format(entry.getValue());
			} catch (IllegalArgumentException e) {
				return null;
			}
		default:
			return null;
		}
	}

}
