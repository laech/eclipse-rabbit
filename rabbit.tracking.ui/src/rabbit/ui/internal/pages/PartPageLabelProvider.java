package rabbit.ui.internal.pages;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPartDescriptor;

/**
 * Label provider for a part page.
 */
public class PartPageLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	/** Keys are workbench part id, values may be null. */
	private Map<String, Image> images;
	private PartPage page;
	private Format formatter;

	/**
	 * Constructs a new page.
	 * 
	 * @param page
	 *            The parent.
	 */
	public PartPageLabelProvider(PartPage page) {
		this.page = page;
		images = new HashMap<String, Image>();
		formatter = new DecimalFormat("#0.00");
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if ((columnIndex != 0) || !(element instanceof IWorkbenchPartDescriptor)) {
			return null;
		}

		IWorkbenchPartDescriptor part = (IWorkbenchPartDescriptor) element;
		if (images.containsKey(part.getId())) {
			return images.get(part.getId());

		} else {
			ImageDescriptor des = part.getImageDescriptor();
			Image img = (des != null) ? des.createImage() : null;
			images.put(part.getId(), img);
			return img;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		IWorkbenchPartDescriptor part = (IWorkbenchPartDescriptor) element;
		switch (columnIndex) {
		case 0:
			return part.getLabel();
		case 1:
			return formatter.format(page.getValue(part));
		default:
			return null;
		}
	}

	@Override
	public void dispose() {
		for (Image img : images.values()) {
			if (img != null) {
				img.dispose();
			}
		}
		super.dispose();
	}
}
