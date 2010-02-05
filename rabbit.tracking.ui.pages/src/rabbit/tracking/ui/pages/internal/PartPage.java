package rabbit.tracking.ui.pages.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;

import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.storage.xml.PartDataAccessor;
import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.pages.AbstractGraphTablePage;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractGraphTablePage {

	// Values are usage values (in minutes) for keys.
	private Map<IWorkbenchPartDescriptor, Double> dataMapping;
	private IAccessor dataStore;

	/**
	 * Constructs a new page.
	 */
	public PartPage() {
		super();
		dataStore = new PartDataAccessor();
		dataMapping = new HashMap<IWorkbenchPartDescriptor, Double>();
	}

	@Override public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
	}

	@Override public void update(DisplayPreference p) {
		dataMapping.clear();
		setMaxValue(0);

		IViewRegistry viewReg = PlatformUI.getWorkbench().getViewRegistry();
		IEditorRegistry editReg = PlatformUI.getWorkbench().getEditorRegistry();

		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		int millisToMinutes = 1000 * 60;
		for (Map.Entry<String, Long> entry : data.entrySet()) {

			IWorkbenchPartDescriptor part = viewReg.find(entry.getKey());
			if (part == null) {
				part = editReg.findEditor(entry.getKey());
			}
			if (part == null) {
				part = new UndefinedWorkbenchPartDescriptor(entry.getKey());
			}

			double value = entry.getValue() / (double) millisToMinutes;
			if (Double.compare(value, getMaxValue()) > 0) {
				setMaxValue(value);
			}
			dataMapping.put(part, value);
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override protected String getValueColumnText() {
		return "Usage (Minutes)";
	}

	/**
	 * Gets the usage value of a part.
	 * 
	 * @param part The workbench part.
	 * @return The usage value.
	 */
	double getValue(IWorkbenchPartDescriptor part) {
		Double value = dataMapping.get(part);
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}

	@Override protected TableColumn[] createColumns(Table table) {
		TableColumn nameCol = new TableColumn(table, SWT.LEFT);
		nameCol.setText("Name");
		nameCol.setWidth(200);
		nameCol.setMoveable(true);

		return new TableColumn[] { nameCol };
	}

	@Override protected IStructuredContentProvider createContentProvider() {
		return new PartPageContentProvider();
	}

	@Override protected ITableLabelProvider createLabelProvider() {
		return new PartPageLabelProvider(this);
	}
}
