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

import static rabbit.ui.internal.util.MillisConverter.toDefaultString;

import rabbit.mylyn.internal.ui.pages.TaskPage.ShowMode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.graphics.Image;

public class TaskPageDecoratingLabelProvider extends DecoratingLabelProvider
		implements ITableLabelProvider {

	private TaskPage page;

	public TaskPageDecoratingLabelProvider(TaskPage parent,
			ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
		page = parent;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return super.getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return super.getText(element);
		case 1:
			if ((element instanceof ITaskContainer && page.getShowMode() == ShowMode.TASK_CATEGORY)
					|| (element instanceof ITask && page.getShowMode() == ShowMode.TASK)) {
				return toDefaultString(page.getValue(element));

			} else if (element instanceof TaskResource) {
				TaskResource e = (TaskResource) element;
				if ((e.resource instanceof IProject && page.getShowMode() == ShowMode.PROJECT)
						|| (e.resource instanceof IFolder && page.getShowMode() == ShowMode.FOLDER)
						|| (e.resource instanceof IFile && page.getShowMode() == ShowMode.FILE)) {
					return toDefaultString(page.getValue(element));
				}
			}
		default:
			return null;
		}
	}
}