package rabbit.ui.internal.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.SessionDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.MillisConverter;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractGraphTreePage {

	private IAccessor dataStore;
	private Map<String, Double> model;

	/** Constructs a new page. */
	public SessionPage() {
		dataStore = new SessionDataAccessor();
		model = new LinkedHashMap<String, Double>();
	}

	@Override
	protected TreeColumn[] createColumns(Tree table) {
		TreeColumn dateCol = new TreeColumn(table, SWT.LEFT);
		dateCol.setText("Date");
		dateCol.setWidth(150);
		dateCol.setMoveable(true);

		return new TreeColumn[] { dateCol };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new SessionPageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new SessionPageLabelProvider();
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		model.clear();

		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Long> entry : data.entrySet()) {
			double value = MillisConverter.toHours(entry.getValue());
			model.put(entry.getKey(), value);
			if (Double.compare(value, getMaxValue()) > 0) {
				setMaxValue(value);
			}
		}
		getViewer().setInput(model);
	}

	@Override
	protected String getValueColumnText() {
		return "Usage (Hours)";
	}

	@Override
	double getValue(Object o) {
		if (!(o instanceof String)) {
			return 0;
		}
		Double value = model.get(o);
		return (value == null) ? 0 : value;
	}

}
