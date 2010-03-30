package rabbit.ui.internal.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IFileMapper;
import rabbit.core.storage.LaunchDescriptor;
import rabbit.ui.internal.AbstractTreeContentProvider;
import rabbit.ui.internal.util.LaunchResource;

public class LaunchPageContentProvider extends AbstractTreeContentProvider {

	private IFileMapper fileMapper;
	private Map<LaunchDescriptor, Set<LaunchResource>> launchToProjects;
	private Map<LaunchResource, Set<LaunchResource>> projectToResources;
	private Map<LaunchResource, Set<LaunchResource>> folderToFiles;

	public LaunchPageContentProvider() {
		fileMapper = RabbitCore.getFileMapper();
		launchToProjects = new HashMap<LaunchDescriptor, Set<LaunchResource>>();
		projectToResources = new HashMap<LaunchResource, Set<LaunchResource>>();
		folderToFiles = new HashMap<LaunchResource, Set<LaunchResource>>();
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof LaunchDescriptor) {
			Set<LaunchResource> set = launchToProjects.get(element);
			return set == null ? EMPTY_ARRAY : set.toArray();

		} else if (element instanceof LaunchResource) {
			LaunchResource res = (LaunchResource) element;
			if (res.getResource() instanceof IProject) {
				Set<LaunchResource> set = projectToResources.get(element);
				return set == null ? EMPTY_ARRAY : set.toArray();

			} else if (res.getResource() instanceof IFolder) {
				Set<LaunchResource> set = folderToFiles.get(element);
				return set == null ? EMPTY_ARRAY : set.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren(Object element) {
/*		if (element instanceof LaunchDescriptor) {
			return !launchToProjects.get(element).isEmpty();

		} else if (element instanceof LaunchResource) {
			LaunchResource res = (LaunchResource) element;
			if (res.getResource() instanceof IProject) {
				return !projectToResources.get(element).isEmpty();

			} else if (res.getResource() instanceof IFolder) {
				return !folderToFiles.get(element).isEmpty();
			}
		}*/
		return getChildren(element).length > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null) {
			return;
		}
	
		launchToProjects.clear();
		projectToResources.clear();
		folderToFiles.clear();

		Collection<LaunchDescriptor> input = (Collection<LaunchDescriptor>) newInput;
		for (LaunchDescriptor launch : input) {
			for (String fileId : launch.getFileIds()) {
				IFile file = fileMapper.getFile(fileId);
				if (file == null) {
					file = fileMapper.getExternalFile(fileId);
				}
				if (file == null) {
					continue;
				}

				LaunchResource fileElement = new LaunchResource(launch, file);

				IProject project = file.getProject();
				LaunchResource projectElement = new LaunchResource(launch, project);
				Set<LaunchResource> projectSet = launchToProjects.get(launch);
				if (projectSet == null) {
					projectSet = new HashSet<LaunchResource>();
					launchToProjects.put(launch, projectSet);
				}
				projectSet.add(projectElement);

				IContainer folder = file.getParent();
				LaunchResource folderElement = new LaunchResource(launch, folder);

				Set<LaunchResource> resources = projectToResources.get(projectElement);
				if (resources == null) {
					resources = new HashSet<LaunchResource>();
					projectToResources.put(projectElement, resources);
				}

				if (project == folder) {
					resources.add(fileElement);
				} else {
					resources.add(folderElement);
					Set<LaunchResource> fileset = folderToFiles.get(folderElement);
					if (fileset == null) {
						fileset = new HashSet<LaunchResource>();
						folderToFiles.put(folderElement, fileset);
					}
					fileset.add(fileElement);
				}
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((Collection<?>) inputElement).toArray();
	}
}
