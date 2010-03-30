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

/**
 * Content provider for a {@link LaunchPage}.
 */
public class LaunchPageContentProvider extends AbstractTreeContentProvider {

	private IFileMapper fileMapper;
	private Map<LaunchDescriptor, Set<LaunchResource>> launchToProjects;
	private Map<LaunchResource, Set<LaunchResource>> projectToResources;
	private Map<LaunchResource, Set<IFile>> folderToFiles;

	/**
	 * Constructs a new content provider.
	 */
	public LaunchPageContentProvider() {
		fileMapper = RabbitCore.getFileMapper();
		launchToProjects = new HashMap<LaunchDescriptor, Set<LaunchResource>>();
		projectToResources = new HashMap<LaunchResource, Set<LaunchResource>>();
		folderToFiles = new HashMap<LaunchResource, Set<IFile>>();
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
				Set<IFile> set = folderToFiles.get(element);
				return set == null ? EMPTY_ARRAY : set.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof LaunchDescriptor) {
			Set<LaunchResource> set = launchToProjects.get(element);
			return set == null ? false : !set.isEmpty();

		} else if (element instanceof LaunchResource) {
			LaunchResource res = (LaunchResource) element;
			if (res.getResource() instanceof IProject) {
				Set<LaunchResource> set = projectToResources.get(element);
				return set == null ? false : !set.isEmpty();

			} else if (res.getResource() instanceof IFolder) {
				Set<IFile> set = folderToFiles.get(element);
				return set == null ? false : !set.isEmpty();
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		launchToProjects.clear();
		projectToResources.clear();
		folderToFiles.clear();
		
		if (newInput == null) {
			return;
		}

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
					Set<IFile> fileset = folderToFiles.get(folderElement);
					if (fileset == null) {
						fileset = new HashSet<IFile>();
						folderToFiles.put(folderElement, fileset);
					}
					fileset.add(file);
				}
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((Collection<?>) inputElement).toArray();
	}
}
