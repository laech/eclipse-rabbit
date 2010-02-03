package rabbit.tracking.ui.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.storage.xml.SessionDataAccessor;
import rabbit.tracking.ui.DisplayPreference;

public class SessionPage extends AbstractGraphicalTablePage {
	
	private IAccessor dataStore;
	
	public SessionPage() {
		dataStore = new SessionDataAccessor();
	}

	@Override
	protected TableColumn[] createColumns(Table table) {
		TableColumn dateCol = new TableColumn(table, SWT.LEFT);
		dateCol.setText("Date");
		dateCol.setWidth(150);
		dateCol.setMoveable(true);
		
		return new TableColumn[] { dateCol };
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new SessionPageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new SessionPageLabelProvider();
	}

	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_ETOOL_HOME_NAV);
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxUsage(0);
		Map<String, Double> model = new LinkedHashMap<String, Double>();
		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		
		int millisToHours = 1000 * 60 * 60;
		for (Map.Entry<String, Long> entry : data.entrySet()) {
			double value = entry.getValue() / (double) millisToHours;
			model.put(entry.getKey(), value);
			if (Double.compare(value, getMaxUsage()) > 0) {
				setMaxUsage(value);
			}
		}
		getViewer().setInput(model);
	}

	@Override
	protected String getUsageColumnText() {
		return "Usage (Hours)";
	}

}
