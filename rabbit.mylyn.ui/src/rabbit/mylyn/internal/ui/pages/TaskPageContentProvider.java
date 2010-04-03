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
package rabbit.mylyn.internal.ui.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;

import rabbit.ui.internal.AbstractTreeContentProvider;

@SuppressWarnings("restriction")
public class TaskPageContentProvider extends AbstractTreeContentProvider {

	private TaskPage page;

	public TaskPageContentProvider(TaskPage parent) {
		this.page = parent;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof ITaskContainer || parent instanceof ITask) {
			List<Object> list = new ArrayList<Object>();

			if (parent instanceof ITaskContainer) {
				list.addAll(((ITaskContainer) parent).getChildren());
			}

			if (parent instanceof ITask) {
				list.addAll(page.getTaskResources((ITask) parent));
			}
			return list.toArray();

		} else if (parent instanceof TaskResource) {
			TaskResource res = (TaskResource) parent;
			if (res.resource instanceof IFolder) {
				return page.getFolderResources(res).toArray();

			} else if (res.resource instanceof IProject) {
				return page.getProjectResources(res).toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		return ((Collection<?>) inputElement).toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		switch (page.getShowMode()) {
		case TASK_CATEGORY:
			return false;

		case TASK:
			// Task category:
			if (element instanceof ITaskContainer && !(element instanceof ITask)) {
				return true;
			}
			// Tasks (can contain sub tasks):
			if (element instanceof AbstractTask) {
				return !((AbstractTask) element).getChildren().isEmpty();
			}
			return false;

		case PROJECT:
			return !(element instanceof TaskResource);

		case FOLDER:
			if (element instanceof TaskResource) {
				return ((TaskResource) element).resource instanceof IProject;
			}
			return true;

		default: // FILE:
			if (element instanceof TaskResource) {
				return !(((TaskResource) element).resource instanceof IFile);
			}
			return true;
		}
	}
}