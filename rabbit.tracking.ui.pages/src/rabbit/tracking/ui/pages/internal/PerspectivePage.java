package rabbit.tracking.ui.pages.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.storage.xml.PerspectiveDataAccessor;
import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.pages.AbstractGraphTablePage;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractGraphTablePage {

	private static final int MILLIS_TO_MINUTES = 1000 * 60;

	private IAccessor dataStore;
	private IPerspectiveRegistry registry;
	private Map<IPerspectiveDescriptor, Double> dataMapping;

	public PerspectivePage() {
		super();
		dataStore = new PerspectiveDataAccessor();
		dataMapping = new HashMap<IPerspectiveDescriptor, Double>();
		registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	@Override protected TableColumn[] createColumns(Table table) {
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(150);
		nameColumn.setMoveable(true);

		return new TableColumn[] { nameColumn };
	}

	@Override protected IStructuredContentProvider createContentProvider() {
		return new PerspectivePageContentProvider();
	}

	@Override protected ITableLabelProvider createLabelProvider() {
		return new PerspectivePageLabelProvider(this);
	}

	@Override protected String getValueColumnText() {
		return "Usage (Minutes)";
	}

	@Override public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE);
	}

	@Override public void update(DisplayPreference p) {
		setMaxValue(0);
		dataMapping.clear();
		System.out.println(TimeUnit.MILLISECONDS.toMinutes(1281387));
		Map<String, Long> map = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Long> entry : map.entrySet()) {
			IPerspectiveDescriptor pd = registry.findPerspectiveWithId(entry.getKey());
			double value = entry.getValue().doubleValue() / MILLIS_TO_MINUTES;
			dataMapping.put(pd, value);
			if (Double.compare(value, getMaxValue()) > 0)
				setMaxValue(value);
		}
		getViewer().setInput(dataMapping.keySet());
	}

	/**
	 * Gets the usage value of a perspective.
	 * 
	 * @param p The perspective.
	 * @return The usage value.
	 */
	double getValue(IPerspectiveDescriptor p) {
		Double value = dataMapping.get(p);
		return (value == null) ? 0 : value;
	}

}
