package rabbit.tracking.ui.pages.internal;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.storage.xml.SessionDataAccessor;
import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.pages.AbstractGraphTablePage;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractGraphTablePage {

	private IAccessor dataStore;

	/** Constructs a new page. */
	public SessionPage() {
		dataStore = new SessionDataAccessor();
	}

	@Override protected TableColumn[] createColumns(Table table) {
		TableColumn dateCol = new TableColumn(table, SWT.LEFT);
		dateCol.setText("Date");
		dateCol.setWidth(150);
		dateCol.setMoveable(true);

		return new TableColumn[] { dateCol };
	}

	@Override protected IStructuredContentProvider createContentProvider() {
		return new SessionPageContentProvider();
	}

	@Override protected ITableLabelProvider createLabelProvider() {
		return new SessionPageLabelProvider();
	}

	@Override public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
	}

	@Override public void update(DisplayPreference p) {
		setMaxValue(0);
		Map<String, Double> model = new LinkedHashMap<String, Double>();
		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());

		int millisToHours = 1000 * 60 * 60;
		for (Map.Entry<String, Long> entry : data.entrySet()) {
			double value = entry.getValue() / (double) millisToHours;
			model.put(entry.getKey(), value);
			if (Double.compare(value, getMaxValue()) > 0) {
				setMaxValue(value);
			}
		}
		getViewer().setInput(model);
	}

	@Override protected String getValueColumnText() {
		return "Usage (Hours)";
	}

}
