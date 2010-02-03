package rabbit.tracking.ui.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
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

public class PartPage extends AbstractGraphicalTablePage {
	
	private IAccessor dataStore;
	
	private Map<IWorkbenchPartDescriptor, Double> dataMapping;
	
	public PartPage() {
		super();
		dataStore = new PartDataAccessor();
		dataMapping = new HashMap<IWorkbenchPartDescriptor, Double>();
	}

	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_DEF_VIEW);
	}

	@Override
	public void update(DisplayPreference p) {
		dataMapping.clear();
		setMaxUsage(0);
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
			if (Double.compare(value, getMaxUsage()) > 0) {
				setMaxUsage(value);
			}
			dataMapping.put(part, value);
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override
	protected String getUsageColumnText() {
		return "Usage (Minutes)";
	}

	double getUsage(IWorkbenchPartDescriptor part) {
		Double value = dataMapping.get(part);
		if (value == null) {
			return 0;
		} else {
			return value;
		}
	}

	@Override
	protected TableColumn[] createColumns(Table table) {
		TableColumn nameCol = new TableColumn(table, SWT.LEFT);
		nameCol.setText("Name");
		nameCol.setWidth(200);
		nameCol.setMoveable(true);

		return new TableColumn[] { nameCol };
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new PartPageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new PartPageLabelProvider(this);
	}
}
