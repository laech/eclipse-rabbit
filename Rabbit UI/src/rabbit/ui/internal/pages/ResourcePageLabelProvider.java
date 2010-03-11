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

import static rabbit.ui.internal.util.MillisConverter.toDefaultString;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import rabbit.ui.internal.pages.ResourcePage.ShowMode;

/**
 * Label provider for a {@link FilePage}.
 */
public class ResourcePageLabelProvider extends BaseLabelProvider implements ITableLabelProvider,
		IColorProvider {

	/** Maps the given path of a resource to an image (may be null). */
	private final Map<String, Image> resourceMap;
	/** Maps the given id of an editor to an image (may be null). */
	private final Map<String, Image> editorMap;

	private final Image projectImg;
	private final Image folderImg;
	private final Image fileImg;

	private final Color deletedResourceColor;

	private ResourcePage page;

	public ResourcePageLabelProvider(ResourcePage parent) {
		deletedResourceColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(
				SWT.COLOR_DARK_GRAY);

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		projectImg = images.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		folderImg = images.getImage(ISharedImages.IMG_OBJ_FOLDER);
		fileImg = images.getImage(ISharedImages.IMG_OBJ_FILE);

		editorMap = new HashMap<String, Image>();
		resourceMap = new HashMap<String, Image>();

		page = parent;
	}

	@Override
	public void dispose() {
		for (Image img : resourceMap.values()) {
			if (img != null && !img.isDisposed()) {
				img.dispose();
			}
		}

		for (Image img : editorMap.values()) {
			if (img != null && !img.isDisposed()) {
				img.dispose();
			}
		}
		super.dispose();
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof IResource) || columnIndex != 0) {
			return null;
		}

		IResource resource = (IResource) element;
		int type = resource.getType();
		switch (type) {
		case IResource.PROJECT:
			return projectImg;
		case IResource.FOLDER:
			return folderImg;
		case IResource.FILE:
			if (resourceMap.containsKey(resource.getFullPath().toString())) {
				return resourceMap.get(resource.getFullPath().toString());
			}

			IEditorDescriptor editor = IDE.getDefaultEditor((IFile) resource);
			if (editor == null) {
				resourceMap.put(resource.getFullPath().toString(), fileImg);
				return fileImg;
			}

			if (editorMap.containsKey(editor.getId())) {
				return editorMap.get(editor.getId());
			}

			ImageDescriptor des = editor.getImageDescriptor();
			Image img = (des == null) ? null : des.createImage();
			editorMap.put(editor.getId(), img);
			return img;
		default:
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof IResource)) {
			return null;
		}
		IResource resource = (IResource) element;
		switch (columnIndex) {
		case 0:
			if (resource instanceof IFolder) {
				return resource.getProjectRelativePath().toString();
			} else {
				return resource.getName();
			}
		case 1:
			if ((resource instanceof IProject && page.getShowMode() == ShowMode.PROJECT)
					|| (resource instanceof IFolder && page.getShowMode() == ShowMode.FOLDER)
					|| (resource instanceof IFile && page.getShowMode() == ShowMode.FILE)) {

				return toDefaultString(page.getValue(resource));
			}
		default:
			return null;
		}
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof IResource && !((IResource) element).exists()) {
			return deletedResourceColor;
		} else {
			return null;
		}
	}
}