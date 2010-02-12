package rabbit.ui.internal.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.PerspectiveDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractGraphTreePage {

	private IAccessor dataStore;
	private IPerspectiveRegistry registry;
	private Map<IPerspectiveDescriptor, Double> dataMapping;

	public PerspectivePage() {
		super();
		dataStore = new PerspectiveDataAccessor();
		dataMapping = new HashMap<IPerspectiveDescriptor, Double>();
		registry = PlatformUI.getWorkbench().getPerspectiveRegistry();
	}

	@Override
	protected TreeColumn[] createColumns(Tree t) {
		TreeColumn nameColumn = new TreeColumn(t, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(150);
		nameColumn.setMoveable(true);

		return new TreeColumn[] { nameColumn };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new CollectionContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new PerspectivePageLabelProvider(this);
	}

	@Override
	protected String getValueColumnText() {
		return "Usage (Minutes)";
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DEF_PERSPECTIVE);
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		dataMapping.clear();
		Map<String, Long> map = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Long> entry : map.entrySet()) {
			IPerspectiveDescriptor pd = registry.findPerspectiveWithId(entry.getKey());
			if (pd == null) {
				pd = new UndefinedPerspectiveDescriptor(entry.getKey());
			}
			double value = MillisConverter.toMinutes(entry.getValue());
			dataMapping.put(pd, value);
			if (Double.compare(value, getMaxValue()) > 0) {
				setMaxValue(value);
			}
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override
	double getValue(Object o) {
		if (!(o instanceof IPerspectiveDescriptor)) {
			return 0;
		}
		Double value = dataMapping.get(o);
		return (value == null) ? 0 : value;
	}

}
