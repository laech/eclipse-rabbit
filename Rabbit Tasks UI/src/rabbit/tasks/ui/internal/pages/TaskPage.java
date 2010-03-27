/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.tasks.ui.internal.pages;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IFileMapper;
import rabbit.tasks.core.TaskCore;
import rabbit.tasks.core.TaskId;
import rabbit.tasks.ui.internal.util.MissingTask;
import rabbit.tasks.ui.internal.util.MissingTaskCategory;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.pages.AbstractTreeViewerPage;

/**
 * A page for displaying time spent working on different tasks and resources.
 */
@SuppressWarnings("restriction")
public class TaskPage extends AbstractTreeViewerPage {

	public static enum ShowMode {
		FILE, FOLDER, PROJECT, TASK, TASK_CATEGORY
	}

	private ShowMode mode = ShowMode.TASK;

	private IAccessor<Map<TaskId, Map<String, Long>>> accessor;
	protected final IFileMapper resourceMapper;

	protected Map<ITask, Set<TaskResource>> taskToProjects;
	protected Map<TaskResource, Set<TaskResource>> projectToResources;
	protected Map<TaskResource, Set<TaskResource>> folderToFiles;
	protected Map<TaskResource, Long> fileToValue;

	private final IAction collapseAllAction = new Action("Collapse All") {
		@Override
		public void run() {
			getViewer().collapseAll();
		}
	};

	private final IAction expandAllAction = new Action("Expand All") {
		@Override
		public void run() {
			getViewer().expandAll();
		}
	};

	private final IAction showFilesAction = new Action("Show Files", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			enableCheckBoxAction(this);
			setShowMode(ShowMode.FILE);
		}
	};

	private final IAction showFoldersAction = new Action("Show Folders", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			enableCheckBoxAction(this);
			setShowMode(ShowMode.FOLDER);
		}
	};

	private final IAction showProjectsAction = new Action("Show Projects", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			enableCheckBoxAction(this);
			setShowMode(ShowMode.PROJECT);
		}
	};

	private final IAction showTasksAction = new Action("Show Tasks", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			enableCheckBoxAction(this);
			setShowMode(ShowMode.TASK);
		}
	};

	private final IAction showTaskCategoriesAction = new Action("Show Task Categories", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			enableCheckBoxAction(this);
			setShowMode(ShowMode.TASK_CATEGORY);
		}
	};

	private final Set<IAction> checkboxActions;

	public TaskPage() {
		super();
		accessor = TaskCore.getTaskDataAccessor();
		resourceMapper = RabbitCore.getFileMapper();

		taskToProjects = new HashMap<ITask, Set<TaskResource>>();
		projectToResources = new HashMap<TaskResource, Set<TaskResource>>();
		folderToFiles = new HashMap<TaskResource, Set<TaskResource>>();
		fileToValue = new HashMap<TaskResource, Long>();

		checkboxActions = new HashSet<IAction>();
		checkboxActions.add(showFilesAction);
		checkboxActions.add(showFoldersAction);
		checkboxActions.add(showProjectsAction);
		checkboxActions.add(showTasksAction);
		checkboxActions.add(showTaskCategoriesAction);
	}

	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		getViewer().addFilter(createViewerFilter());
	}

	protected ViewerFilter createViewerFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parent, Object element) {

				if (getShowMode() != ShowMode.TASK_CATEGORY) {
					// Filter out the categories and tasks that the user
					// never worked on:
					if (element instanceof ITask) {
						// A task also implements ITaskContainer, so we check
						// ITask
						// first:
						return getValueOfTask((ITask) element) > 0;
					}
				}

				if (element instanceof ITaskContainer) {
					return getValueOfCategory((ITaskContainer) element) > 0;
				}

				switch (getShowMode()) {
				case FILE:
					return true;
				case FOLDER:
					if (element instanceof TaskResource) {
						return !(((TaskResource) element).resource instanceof IFile);
					}
					return true;
				case PROJECT:
					if (element instanceof TaskResource) {
						return ((TaskResource) element).resource instanceof IProject;
					}
					return true;
				case TASK:
					return !(element instanceof TaskResource);
				case TASK_CATEGORY:
					return element instanceof ITaskContainer && !(element instanceof ITask);
				default:
					return false;
				}
			}
		};
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		expandAllAction.setImageDescriptor(SharedImages.EXPAND_ALL);
		IContributionItem expandAll = new ActionContributionItem(expandAllAction);
		toolBar.add(expandAll);

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		ImageDescriptor img = images.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
		collapseAllAction.setImageDescriptor(img);
		IContributionItem collapseAll = new ActionContributionItem(collapseAllAction);
		toolBar.add(collapseAll);

		Separator sep = new Separator();
		toolBar.add(sep);

		showTaskCategoriesAction.setChecked(getShowMode() == ShowMode.TASK_CATEGORY);
		showTaskCategoriesAction.setImageDescriptor(TasksUiImages.CATEGORY_UNCATEGORIZED);
		IContributionItem showTaskCategories = new ActionContributionItem(showTaskCategoriesAction);
		toolBar.add(showTaskCategories);

		showTasksAction.setChecked(getShowMode() == ShowMode.TASK);
		showTasksAction.setImageDescriptor(TasksUiImages.TASK);
		IContributionItem showTasks = new ActionContributionItem(showTasksAction);
		toolBar.add(showTasks);

		img = images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
		showProjectsAction.setImageDescriptor(img);
		showProjectsAction.setChecked(getShowMode() == ShowMode.PROJECT);
		IContributionItem showProjects = new ActionContributionItem(showProjectsAction);
		toolBar.add(showProjects);

		img = images.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		showFoldersAction.setImageDescriptor(img);
		showFoldersAction.setChecked(getShowMode() == ShowMode.FOLDER);
		IContributionItem showFolders = new ActionContributionItem(showFoldersAction);
		toolBar.add(showFolders);

		img = images.getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
		showFilesAction.setImageDescriptor(img);
		showFilesAction.setChecked(getShowMode() == ShowMode.FILE);
		IContributionItem showFiles = new ActionContributionItem(showFilesAction);
		toolBar.add(showFiles);

		return new IContributionItem[] {
				expandAll, collapseAll, sep, showTaskCategories, showTasks, showProjects, showFolders, showFiles };
	}

	public long getMaxCategoryValue() {
		long max = 0;
		for (ITaskContainer c : TasksUiPlugin.getTaskList().getRootElements()) {
			long value = getValueOfCategory(c);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxFileValue() {
		long max = 0;
		for (long value : fileToValue.values()) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxFolderValue() {
		long max = 0;
		for (Entry<TaskResource, Set<TaskResource>> entry : folderToFiles.entrySet()) {
			long value = getValueOfFolder(entry.getKey());
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxProjectValue() {
		long max = 0;
		for (Entry<TaskResource, Set<TaskResource>> entry : projectToResources.entrySet()) {
			long value = getValueOfProject(entry.getKey());
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public long getMaxTaskValue() {
		long max = 0;
		for (ITask task : taskToProjects.keySet()) {
			long value = getValueOfTask(task);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	public Collection<TaskResource> getTaskResources(ITask task) {
		Set<TaskResource> set = taskToProjects.get(task);
		if (set != null) {
			return Collections.unmodifiableSet(set);
		}
		return Collections.emptySet();
	}

	public Collection<TaskResource> getFolderResources(TaskResource folder) {
		Set<TaskResource> set = folderToFiles.get(folder);
		return (set == null) ? Collections.<TaskResource> emptySet()
				: Collections.unmodifiableSet(set);
	}

	public Collection<TaskResource> getProjectResources(TaskResource project) {
		Set<TaskResource> set = projectToResources.get(project);
		return (set == null) ? Collections.<TaskResource> emptySet()
				: Collections.unmodifiableSet(set);
	}

	public ShowMode getShowMode() {
		return mode;
	}

	@Override
	public long getValue(Object o) {
		if (o instanceof TaskResource) {
			TaskResource res = (TaskResource) o;
			if (res.resource instanceof IProject) {
				return getValueOfProject(res);

			} else if (res.resource instanceof IFolder) {
				return getValueOfFolder(res);

			} else {
				return getValueOfFile(res);
			}

		} else if (o instanceof ITask) {
			return getValueOfTask((ITask) o);

		} else if (o instanceof ITaskContainer) {
			return getValueOfCategory((ITaskContainer) o);
		}
		return 0;
	}

	public long getValueOfCategory(ITaskContainer category) {
		long value = 0;
		for (ITask task : category.getChildren()) {
			value += getValueOfTask(task);
		}
		return value;
	}

	// public long getValueOfResource(TaskResource element) {
	// if (element.resource.getType() == IResource.FILE) {
	// Long value = fileToValue.get(element);
	// return (value == null) ? 0 : value;
	//
	// } else {
	// long value = 0;
	// Set<TaskResource> children = resourceToResources.get(element);
	// if (children == null) {
	// return 0;
	// }
	// for (TaskResource child : children) {
	// value += getValueOfResource(child);
	// }
	// return value;
	// }
	// }

	public long getValueOfTask(ITask task) {
		long value = 0;
		Set<TaskResource> children = taskToProjects.get(task);
		if (children != null) {
			for (TaskResource child : children) {
				value += getValueOfProject(child);
			}
		}
		if (task instanceof AbstractTask) {
			for (ITask child : ((AbstractTask) task).getChildren()) {
				value += getValueOfTask(child);
			}
		}
		return value;
	}

	public long getValueOfFile(TaskResource file) {
		Long value = fileToValue.get(file);
		return value == null ? 0 : value;
	}

	public long getValueOfFolder(TaskResource folder) {
		long value = 0;
		for (TaskResource file : getFolderResources(folder)) {
			value += getValueOfFile(file);
		}
		return value;
	}

	public long getValueOfProject(TaskResource project) {
		long value = 0;
		for (TaskResource res : getProjectResources(project)) {
			if (res.resource instanceof IFolder) {
				value += getValueOfFolder(res);
			} else {
				value += getValueOfFile(res);
			}
		}
		return value;
	}

	public void setShowMode(ShowMode newMode) {
		if (mode == newMode) {
			return;
		}

		collapseAllAction.setEnabled(!(newMode == ShowMode.TASK_CATEGORY));
		expandAllAction.setEnabled(collapseAllAction.isEnabled());

		mode = newMode;
		updateMaxValue();
		getViewer().refresh();
	}

	@Override
	public boolean shouldPaint(Object o) {
		switch (getShowMode()) {
		case TASK_CATEGORY:
			return (o instanceof ITaskContainer) && !(o instanceof ITask);
		case TASK:
			return (o instanceof ITask);
		case PROJECT:
		case FOLDER:
		case FILE:
			if (false == (o instanceof TaskResource)) {
				return false;
			}
			TaskResource e = (TaskResource) o;
			return (e.resource instanceof IProject && getShowMode() == ShowMode.PROJECT)
					|| (e.resource instanceof IFolder && getShowMode() == ShowMode.FOLDER)
					|| (e.resource instanceof IFile && getShowMode() == ShowMode.FILE);
		default:
			return false;
		}
	}

	@Override
	public void update(DisplayPreference p) {
		Object[] elements = getViewer().getExpandedElements();
		doUpdate(accessor.getData(p.getStartDate(), p.getEndDate()));
		try {
			getViewer().setExpandedElements(elements);
		} catch (IllegalArgumentException e) {
			// Do nothing.
		}
	}

	@Override
	protected void createColumns(TreeViewer viewer) {
		TreeLabelComparator textSorter = new TreeLabelComparator(viewer);
		TreeLabelComparator valueSorter = createValueSorterForTree(viewer);

		int[] widths = new int[] { 300, 150 };
		int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
		String[] names = new String[] { "Name", "Time Spent" };

		for (int i = 0; i < names.length; i++) {
			TreeColumn column = new TreeColumn(viewer.getTree(), styles[i]);
			column.setText(names[i]);
			column.setWidth(widths[i]);
			column.addSelectionListener((names.length - 1 == i) ? valueSorter
					: textSorter);
		}
	}

	@Override
	protected TreeLabelComparator createComparator(TreeViewer viewer) {
		return new TreeLabelComparator(viewer) {

			@Override
			public int category(Object e) {
				if (e instanceof ITaskContainer) {
					return 1;
				} else if (e instanceof ITask) {
					return 2;
				} else if (e instanceof TaskResource) {
					TaskResource element = (TaskResource) e;
					if (element.resource instanceof IProject) {
						return 3;
					} else if (element.resource instanceof IFolder) {
						return 4;
					} else if (element.resource instanceof IFile) {
						return 5;
					}
				}
				return 0;
			}
		};
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new TaskPageContentProvider(this);
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new TaskPageDecoratingLabelProvider(this, new TaskPageLabelProvider(),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

	private void doUpdate(Map<TaskId, Map<String, Long>> data) {
		setMaxValue(0);

		taskToProjects.clear();
		projectToResources.clear();
		folderToFiles.clear();
		fileToValue.clear();

		MissingTaskCategory undefinedCategory = MissingTaskCategory.getCategory();
		undefinedCategory.getChildren().clear();

		IRepositoryModel repo = TasksUi.getRepositoryModel();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (Entry<TaskId, Map<String, Long>> taskEn : data.entrySet()) {

			TaskId id = taskEn.getKey();
			ITask task = repo.getTask(id.getHandleIdentifier());

			// Same handle id but different creation date, which means different
			// tasks.
			if (task != null && !task.getCreationDate().equals(id.getCreationDate())) {
				task = null;
			}
			if (task == null) {
				task = new MissingTask(id);
				undefinedCategory.getChildren().add(task);
			}

			for (Entry<String, Long> fileEn : taskEn.getValue().entrySet()) {
				String pathStr = resourceMapper.getPath(fileEn.getKey());
				if (pathStr == null) {
					pathStr = resourceMapper.getExternalPath(fileEn.getKey());
				}
				if (pathStr == null) {
					continue;
				}

				IFile file = root.getFile(Path.fromPortableString(pathStr));
				TaskResource fileElement = new TaskResource(task, file);
				Long oldValue = fileToValue.get(fileElement);
				if (oldValue == null) {
					oldValue = Long.valueOf(0);
				}
				fileToValue.put(fileElement, fileEn.getValue() + oldValue);

				IProject project = file.getProject();
				TaskResource projectElement = new TaskResource(task, project);
				Set<TaskResource> projectSet = taskToProjects.get(task);
				if (projectSet == null) {
					projectSet = new HashSet<TaskResource>();
					taskToProjects.put(task, projectSet);
				}
				projectSet.add(projectElement);

				IContainer folder = file.getParent();
				TaskResource folderElement = new TaskResource(task, folder);

				Set<TaskResource> resources = projectToResources.get(projectElement);
				if (resources == null) {
					resources = new HashSet<TaskResource>();
					projectToResources.put(projectElement, resources);
				}

				if (project == folder) {
					resources.add(fileElement);
				} else {
					resources.add(folderElement);
					Set<TaskResource> fileset = folderToFiles.get(folderElement);
					if (fileset == null) {
						fileset = new HashSet<TaskResource>();
						folderToFiles.put(folderElement, fileset);
					}
					fileset.add(fileElement);
				}
			}
		}
		updateMaxValue();

		Set<Object> objects = new HashSet<Object>(TasksUiPlugin.getTaskList().getRootElements());
		objects.add(undefinedCategory);
		getViewer().setInput(objects);
	}

	private void enableCheckBoxAction(IAction action) {
		for (IAction act : checkboxActions) {
			act.setChecked(act == action);
		}
	}

	private void updateMaxValue() {
		switch (getShowMode()) {
		case FILE:
			setMaxValue(getMaxFileValue());
			break;
		case FOLDER:
			setMaxValue(getMaxFolderValue());
			break;
		case PROJECT:
			setMaxValue(getMaxProjectValue());
			break;
		case TASK:
			setMaxValue(getMaxTaskValue());
			break;
		default: // TASK_CATEGORY:
			setMaxValue(getMaxCategoryValue());
			break;
		}
	}
}
