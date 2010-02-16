package rabbit.ui.internal.pages;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import rabbit.ui.internal.util.MillisConverter;

/**
 * A label provider for a session page.
 */
public class SessionPageLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	private SessionPage parent;

	public SessionPageLabelProvider(SessionPage parent) {
		this.parent = parent;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return element.toString();
		case 1:
			try {
				return MillisConverter.toDefaultString(parent.getValue(element));
			} catch (IllegalArgumentException e) {
				return null;
			}
		default:
			return null;
		}
	}

}
