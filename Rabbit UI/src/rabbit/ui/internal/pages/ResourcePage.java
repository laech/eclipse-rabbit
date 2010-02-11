package rabbit.ui.internal.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IResourceManager;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

/**
 * A page for displaying resource usage.
 */
public abstract class ResourcePage extends AbstractGraphTreePage {

	private IAccessor accessor;
	private IResourceManager resourceMapper;

	public ResourcePage() {
		super();
		accessor = new FileDataAccessor();
		resourceMapper = RabbitCore.getDefault().getResourceManager();
	}

	@Override
	public void update(DisplayPreference p) {
		Map<String, Long> rawData = accessor.getData(p.getStartDate(), p.getEndDate());
		Map<String, ResourceElement> ls = new HashMap<String, ResourceElement>(rawData.size());
		for (Map.Entry<String, Long> entry : rawData.entrySet()) {
			String pathString = resourceMapper.getFilePath(entry.getKey());
			if (pathString == null) {
				continue;
			}

			IPath path = Path.fromPortableString(pathString);
			if (path.segmentCount() <= 1) {
				continue;
			}

			String project = path.segment(0);
			ResourceElement l = ls.get(project);
			if (l == null) {
				l = new ProjectElement(path.uptoSegment(1));
				ls.put(project, l);
			}

			if (path.segmentCount() >= 3) {
				l = l.insert(new FolderElement(path.removeLastSegments(1)));
			}

			l.insert(new FileElement(path, MillisConverter.toMinutes(entry.getValue())));
		}
		getViewer().setInput(ls.values());
		getViewer().expandAll();
	}

	@Override
	protected String getValueColumnText() {
		return "Time (Minutes)";
	}

	@Override
	protected TreeColumn[] createColumns(Tree tree) {
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setText("Edited Resources");
		nameColumn.setWidth(200);
		nameColumn.setMoveable(true);
		return new TreeColumn[] { nameColumn };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new AbstractTreeContentProvider() {
			@Override
			public Object[] getChildren(Object parent) {
				if (parent instanceof Collection<?>) {
					return ((Collection<?>) parent).toArray();
				} else if (parent instanceof ResourceElement) {
					return ((ResourceElement) parent).getChildren().toArray();
				}
				return EMPTY_ARRAY;
			}

			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public boolean hasChildren(Object element) {
				return (element instanceof ResourceElement)
						&& !((ResourceElement) element).getChildren().isEmpty();
			}
		};
	}
}
