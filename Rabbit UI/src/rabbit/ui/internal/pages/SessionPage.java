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

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractGraphTreePage {

	private IAccessor dataStore;
	private Map<String, Long> model;

	/** Constructs a new page. */
	public SessionPage() {
		dataStore = new SessionDataAccessor();
		model = new LinkedHashMap<String, Long>();
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
		return new SessionPageLabelProvider(this);
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_HOME_NAV);
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		model.clear();
		model = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (long value : model.values()) {
			if (value > getMaxValue()) {
				setMaxValue(value);
			}
		}
		getViewer().setInput(model.keySet());
	}

	@Override
	protected String getValueColumnText() {
		return "Duration";
	}

	@Override
	long getValue(Object o) {
		Long value = model.get(o);
		return (value == null) ? 0 : value;
	}
}
