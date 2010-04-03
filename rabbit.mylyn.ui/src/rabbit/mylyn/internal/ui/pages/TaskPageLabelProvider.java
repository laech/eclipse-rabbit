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

import rabbit.mylyn.internal.ui.util.MissingTask;
import rabbit.mylyn.internal.ui.util.MissingTaskCategory;
import rabbit.ui.internal.pages.ResourcePageLabelProvider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class TaskPageLabelProvider extends LabelProvider
		implements IColorProvider, IFontProvider {

	private final ResourcePageLabelProvider resourceProvider;
	private final TaskElementLabelProvider taskProvider;
	
	private final Image missingCategoryImg;
	private final Image missingTaskImg;

	public TaskPageLabelProvider() {
		missingCategoryImg = TasksUiImages.CATEGORY.createImage();
		missingTaskImg = TasksUiImages.TASK.createImage();
		taskProvider = new TaskElementLabelProvider();
		resourceProvider = new ResourcePageLabelProvider() {

			@Override
			protected ImageDescriptor decorateImage(ImageDescriptor input, Object element) {
				if (element instanceof TaskResource) {
					return super.decorateImage(input,
							((TaskResource) element).resource);
				}
				return super.decorateImage(input, element);
			}

			@Override
			protected String decorateText(String input, Object element) {
				if (element instanceof TaskResource) {
					return super.decorateText(input,
							((TaskResource) element).resource);
				}
				return super.decorateText(input, element);
			}
		};
	}

	@Override
	public void dispose() {
		super.dispose();
		missingCategoryImg.dispose();
		missingTaskImg.dispose();
		taskProvider.dispose();
		resourceProvider.dispose();
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof TaskResource) {
			return resourceProvider.getBackground(((TaskResource) element).resource);
		}
		return taskProvider.getBackground(element);
	}

	@Override
	public Font getFont(Object element) {
		return taskProvider.getFont(element);
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof TaskResource) {
			return resourceProvider.getForeground(((TaskResource) element).resource);
		}
		return taskProvider.getForeground(element);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof MissingTaskCategory)
			return missingCategoryImg;
		
		else if (element instanceof MissingTask)
			return missingTaskImg;
		
		else if (element instanceof TaskResource)
			return resourceProvider.getImage(((TaskResource) element).resource);
		
		else
			return taskProvider.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof TaskResource) {
			return resourceProvider.getText(((TaskResource) element).resource);
		}
		return taskProvider.getText(element);
	}
}
