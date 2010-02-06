package rabbit.ui.internal.pages;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;

/**
 * Label provider for {@link PerspectivePaeg}
 */
public class PerspectivePageLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	private Map<String, Image> images;
	private PerspectivePage parent;
	private Format formatter;

	public PerspectivePageLabelProvider(PerspectivePage parent) {
		this.parent = parent;
		formatter = new DecimalFormat("#0.00");
		images = new HashMap<String, Image>();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ((columnIndex != 0) || !(element instanceof IPerspectiveDescriptor))
			return null;

		IPerspectiveDescriptor p = (IPerspectiveDescriptor) element;
		Image img = images.get(p.getId());
		if (img == null) {
			img = p.getImageDescriptor().createImage();
			images.put(p.getId(), img);
		}
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

}
