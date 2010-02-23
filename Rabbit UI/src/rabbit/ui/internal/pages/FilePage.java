package rabbit.ui.internal.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IResourceManager;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.ColumnComparator;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

/**
 * A page for displaying time spent working on different files.
 */
public class FilePage extends AbstractGraphTreePage {

	private IAccessor accessor;
	private TreeColumn nameColumn;
	private IResourceManager resourceMapper;
	protected Collection<ResourceElement> data;

	public FilePage() {
		super();
		accessor = new FileDataAccessor();
		resourceMapper = RabbitCore.getDefault().getResourceManager();
		data = new ArrayList<ResourceElement>();
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);

		Map<String, Long> rawData = accessor.getData(p.getStartDate(), p.getEndDate());
		Map<String, ResourceElement> resources = new HashMap<String, ResourceElement>(rawData.size());
		for (Map.Entry<String, Long> entry : rawData.entrySet()) {
			String pathString = resourceMapper.getPath(entry.getKey());
			if (pathString == null) {
				continue;
			}

			IPath path = Path.fromPortableString(pathString);
			if (path.segmentCount() <= 1) {
				continue;
			}

			String project = path.segment(0);
			ResourceElement element = resources.get(project);
			if (element == null) {
				element = new ProjectElement(path.uptoSegment(1));
				resources.put(project, element);
			}
			if (path.segmentCount() >= 3) {
				element = element.insert(new FolderElement(path.removeLastSegments(1)));
			}
			element.insert(new FileElement(path, entry.getValue()));

			if (entry.getValue() > getMaxValue()) {
				setMaxValue(entry.getValue());
			}
		}
		getViewer().setInput((data = resources.values()));
		getViewer().expandAll();
	}

	@Override
	long getValue(Object o) {
		if (o instanceof FileElement) {
			return ((FileElement) o).getValue();
		} else {
			return 0;
		}
	}

	@Override
	protected String getValueColumnText() {
		return "Time Spent";
	}

	@Override
	protected TreeColumn[] createColumns(Tree tree) {
		nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setText("Resource");
		nameColumn.setWidth(200);
		nameColumn.setMoveable(true);
		return new TreeColumn[] { nameColumn };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider(false, false, true);
	}

	@Override
	protected ColumnComparator createComparator(TreeViewer viewer) {
		return new ColumnComparator(viewer) {
			@Override
			public int category(Object element) {
				if (element instanceof FileElement) {
					return 1;
				} else if (element instanceof FolderElement) {
					return 2;
				} else {
					return 0;
				}
			}

			@Override
			protected int doCompare(Viewer v, Object e1, Object e2) {
				if (getSelectedColumn() == nameColumn) {
					if (e1 instanceof FileElement && !(e2 instanceof FileElement)) {
						return -1;
					} else if (!(e1 instanceof FileElement) && e2 instanceof FileElement) {
						return 1;
					} else {
						return super.doCompare(v, e1, e2);
					}
				} else if (getSelectedColumn() == getValueColumn() || getSelectedColumn() == getGraphColumn()) {
					return (getValue(e1) > getValue(e2)) ? 1 : -1;
				}
				return super.doCompare(v, e1, e2);
			}
		};
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}
}
