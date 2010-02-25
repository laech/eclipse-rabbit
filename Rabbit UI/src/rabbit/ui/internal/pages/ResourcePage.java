package rabbit.ui.internal.pages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IResourceManager;
import rabbit.core.storage.xml.FileDataAccessor;
import rabbit.ui.ColumnComparator;
import rabbit.ui.DisplayPreference;

/**
 * A page for displaying time spent working on different files.
 */
public abstract class ResourcePage extends AbstractGraphTreePage {

	private IAccessor accessor;
	private IResourceManager resourceMapper;

	private Map<IProject, Set<IResource>> projectResources;
	private Map<IFolder, Set<IFile>> folderFiles;
	private Map<IFile, Long> fileValues;

	public ResourcePage() {
		super();
		accessor = new FileDataAccessor();
		resourceMapper = RabbitCore.getDefault().getResourceManager();

		projectResources = new HashMap<IProject, Set<IResource>>();
		folderFiles = new HashMap<IFolder, Set<IFile>>();
		fileValues = new HashMap<IFile, Long>();
	}

	@Override
	protected ColumnComparator createComparator(TreeViewer viewer) {
		return new ColumnComparator(viewer) {

			@Override
			public int category(Object element) {
				if (element instanceof IProject) {
					return 1;
				} else if (element instanceof IFolder) {
					return 2;
				} else if (element instanceof IFile) {
					return 3;
				} else {
					return 0;
				}
			}

			@Override
			protected int doCompare(Viewer v, Object e1, Object e2) {
				int cat1 = category(e1);
				int cat2 = category(e2);

				if (cat1 != cat2) {
					return cat1 - cat2;
				}

				if (getSelectedColumn() == getValueColumn() || getSelectedColumn() == getGraphColumn()) {
					return (getValue(e1) > getValue(e2)) ? 1 : -1;
				}
				return super.doCompare(v, e1, e2);
			}
		};
	}

	@Override
	public void update(DisplayPreference p) {
		System.out.println(p.getStartDate().getTime());
		System.out.println(p.getEndDate().getTime());
		doUpdate(accessor.getData(p.getStartDate(), p.getEndDate()));
	}

	private void doUpdate(Map<String, Long> data) {
		setMaxValue(0);

		projectResources.clear();
		folderFiles.clear();
		fileValues.clear();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (Map.Entry<String, Long> entry : data.entrySet()) {
			String pathString = resourceMapper.getPath(entry.getKey());
			if (pathString == null) {
				continue;
			}

			IFile file = root.getFile(Path.fromPortableString(pathString));
			Long oldValue = fileValues.get(file);
			if (oldValue == null) {
				oldValue = Long.valueOf(0);
			}
			fileValues.put(file, entry.getValue() + oldValue);

			IProject project = file.getProject();
			IContainer folder = file.getParent();

			Set<IResource> resources = projectResources.get(project);
			if (resources == null) {
				resources = new HashSet<IResource>();
				projectResources.put(project, resources);
			}

			if (project == folder) {
				resources.add(file);
			} else {
				resources.add(folder);
				Set<IFile> fileset = folderFiles.get(folder);
				if (fileset == null) {
					fileset = new HashSet<IFile>();
					folderFiles.put((IFolder) folder, fileset);
				}
				fileset.add(file);
			}
		}
		getViewer().setInput(projectResources.keySet());
	}

	@Override
	protected String getValueColumnText() {
		return "Time Spent";
	}

	@Override
	protected TreeColumn[] createColumns(Tree tree) {
		TreeColumn nameColumn = new TreeColumn(tree, SWT.LEFT);
		nameColumn.setText("Resource");
		nameColumn.setWidth(200);
		nameColumn.setMoveable(true);
		return new TreeColumn[] { nameColumn };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new ResourcePageContentProvider2(this);
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new ResourcePageLabelProvider2(this, false, false, true);
	}

	public long getValueOfFile(IFile file) {
		Long value = fileValues.get(file);
		return (null == value) ? 0 : value;
	}

	public long getValueOfFolder(IFolder folder) {
		Set<IFile> files = folderFiles.get(folder);
		if (files == null) {
			return 0;
		}
		long value = 0;
		for (IFile file : files) {
			value += getValueOfFile(file);
		}
		return value;
	}

	public long getValueOfProject(IProject project) {
		Set<IResource> resources = projectResources.get(project);
		if (resources == null) {
			return 0;
		}
		long value = 0;
		for (IResource resource : resources) {
			value += (resource instanceof IFile) ? getValueOfFile((IFile) resource) : getValueOfFolder((IFolder) resource);
		}
		return value;
	}

	public long getMaxProjectValue() {
		long max = 0;
		for (IProject project : projectResources.keySet()) {
			long value = getValueOfProject(project);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxFolderValue() {
		long max = 0;
		for (IFolder folder : folderFiles.keySet()) {
			long value = getValueOfFolder(folder);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxFileValue() {
		long max = 0;
		for (long value : fileValues.values()) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public IResource[] getResources(IProject project) {
		Set<IResource> resources = projectResources.get(project);
		if (resources != null) {
			return resources.toArray(new IResource[resources.size()]);
		}
		return new IResource[0];
	}

	public IFile[] getFiles(IFolder folder) {
		Set<IFile> files = folderFiles.get(folder);
		if (files != null) {
			return files.toArray(new IFile[files.size()]);
		}
		return new IFile[0];
	}
}
