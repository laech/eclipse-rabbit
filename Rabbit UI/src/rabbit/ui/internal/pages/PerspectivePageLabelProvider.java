package rabbit.ui.internal.pages;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.internal.UndefinedPerspectiveDescriptor;

/**
 * Label provider for {@link PerspectivePaeg}
 */
public class PerspectivePageLabelProvider extends BaseLabelProvider implements ITableLabelProvider, IColorProvider {

	private Map<String, Image> images;
	private PerspectivePage parent;
	private Format formatter;
	private final Color undefinedColor;

	public PerspectivePageLabelProvider(PerspectivePage parent) {
		this.parent = parent;
		formatter = new DecimalFormat("#0.00");
		images = new HashMap<String, Image>();
		undefinedColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ((columnIndex != 0) || !(element instanceof IPerspectiveDescriptor))
			return null;

		IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
		if (images.containsKey(p.getId())) {
			return images.get(p.getId());
		}
		ImageDescriptor des = p.getImageDescriptor();
		Image img = (des == null) ? null : des.createImage();
		images.put(p.getId(), img);
		return img;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
		switch (columnIndex) {
		case 0:
			return p.getLabel();
		case 1:
			return formatter.format(parent.getValue(p));
		default:
			return null;
		}
	}

	@Override
	public void dispose() {
		for (Image img : images.values()) {
			img.dispose();
		}
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
