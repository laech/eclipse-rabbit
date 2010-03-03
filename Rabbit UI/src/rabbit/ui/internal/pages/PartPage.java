package rabbit.ui.internal.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.PartDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractTableViewerPage {

	private Map<IWorkbenchPartDescriptor, Long> dataMapping;
	private IAccessor dataStore;

	/**
	 * Constructs a new page.
	 */
	public PartPage() {
		super();
		dataStore = new PartDataAccessor();
		dataMapping = new HashMap<IWorkbenchPartDescriptor, Long>();
	}

	@Override
	public void createColumns(TableViewer viewer) {
		TableLabelComparator valueSorter = createValueSorterForTable(viewer);
		TableLabelComparator textSorter = new TableLabelComparator(viewer);

		int[] widths = new int[] { 200, 150 };
		int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
		String[] names = new String[] { "Name", "Usage" };
		for (int i = 0; i < names.length; i++) {
			TableColumn column = new TableColumn(viewer.getTable(), styles[i]);
			column.setText(names[i]);
			column.setWidth(widths[i]);
			column.addSelectionListener(
					(names.length - 1 == i) ? valueSorter : textSorter);
		}
	}

	@Override
	protected IContentProvider createContentProvider() {
		return new CollectionContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new PartPageLabelProvider(this);
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_DEF_VIEW);
	}

	@Override
	public long getValue(Object o) {
		Long value = dataMapping.get(o);
		return (value == null) ? 0 : value;
	}

	@Override
	public void update(DisplayPreference p) {
		dataMapping.clear();
		setMaxValue(0);

		IViewRegistry viewReg = PlatformUI.getWorkbench().getViewRegistry();
		IEditorRegistry editReg = PlatformUI.getWorkbench().getEditorRegistry();

		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Long> entry : data.entrySet()) {

			IWorkbenchPartDescriptor part = viewReg.find(entry.getKey());
			if (part == null) {
				part = editReg.findEditor(entry.getKey());
			}
			if (part == null) {
				part = new UndefinedWorkbenchPartDescriptor(entry.getKey());
			}

			if (entry.getValue() > getMaxValue()) {
				setMaxValue(entry.getValue());
			}
			dataMapping.put(part, entry.getValue());
		}
		getViewer().setInput(dataMapping.keySet());
	}
}
