package rabbit.ui.internal.pages;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;

import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

/**
 * Label provider for {@link PerspectivePaeg}
 */
public class PerspectivePageLabelProvider extends BaseLabelProvider implements ITableLabelProvider, IColorProvider {

	private PerspectiveLabelProvider provider;
	private PerspectivePage parent;
	private final Color undefinedColor;

	public PerspectivePageLabelProvider(PerspectivePage parent) {
		this.parent = parent;
		undefinedColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
		provider = new PerspectiveLabelProvider(false);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return provider.getColumnImage(element, columnIndex);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof IPerspectiveDescriptor)) {
			return null;
		}
		IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
		switch (columnIndex) {
		case 0:
			return provider.getColumnText(element, columnIndex);
		case 1:
			return MillisConverter.toDefaultString(parent.getValue(p));
		default:
			return null;
		}
	}

	@Override
	public void dispose() {
		provider.dispose();
		super.dispose();
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof UndefinedPerspectiveDescriptor) {
			return undefinedColor;
		}
		return null;
	}

}
