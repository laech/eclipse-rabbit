package rabbit.ui.internal.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.core.internal.storage.xml.ResourceData;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.MillisConverter;
import rabbit.ui.internal.ResourceElement;
import rabbit.ui.internal.ResourceElement.ResourceType;
import rabbit.ui.pages.AbstractGraphTreePage;

public abstract class ResourcePage extends AbstractGraphTreePage {

	private IAccessor accessor = new FileDataAccessor();

	public ResourcePage() {
		super();
	}

	@Override
	public void update(DisplayPreference p) {
		Map<String, Long> rawData = accessor.getData(p.getStartDate(), p.getEndDate());
		Map<String, ResourceElement> ls = new HashMap<String, ResourceElement>(rawData.size());
		for (Map.Entry<String, Long> entry : rawData.entrySet()) {
			String path = ResourceData.INSTANCE.getFilePath(entry.getKey());
			if (path == null) {
				continue;
			}

			String project = ResourceElement.getProjectPath(path);
			ResourceElement l = ls.get(project);
			if (l == null) {
				l = ResourceElement.createProject(project);
				ls.put(project, l);
			}

			String folder = ResourceElement.getFolderPath(path);
			if (folder != null) {
				l = l.insert(folder, ResourceType.FOLDER, 0);
			}

			l.insert(path, ResourceType.FILE, MillisConverter.toMinutes(entry.getValue()));

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
				return (element instanceof ResourceElement) && !((ResourceElement) element).getChildren().isEmpty();
			}
		};
	}
}
