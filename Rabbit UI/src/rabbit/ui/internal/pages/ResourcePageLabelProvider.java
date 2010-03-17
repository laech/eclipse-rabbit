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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ResourcePageLabelProvider extends BaseLabelProvider
		implements ILabelProvider, IColorProvider {

	/** Maps the given path of a resource to an image (may be null). */
	private final Map<String, Image> resourceMap;
	/** Maps the given id of an editor to an image (may be null). */
	private final Map<String, Image> editorMap;

	private final Image projectImg;
	private final Image folderImg;
	private final Image fileImg;

	private final Color deletedResourceColor;

	public ResourcePageLabelProvider() {
		deletedResourceColor = PlatformUI.getWorkbench().getDisplay()
				.getSystemColor(SWT.COLOR_DARK_GRAY);

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
		projectImg = images.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		folderImg = images.getImage(ISharedImages.IMG_OBJ_FOLDER);
		fileImg = images.getImage(ISharedImages.IMG_OBJ_FILE);

		editorMap = new HashMap<String, Image>();
		resourceMap = new HashMap<String, Image>();
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
	public Image getImage(Object element) {
		if (!(element instanceof IResource)) {
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
	public String getText(Object element) {
		if (!(element instanceof IResource)) {
			return null;
		}
		IResource resource = (IResource) element;
		if (resource instanceof IFolder) {
			return resource.getProjectRelativePath().toString();
		} else {
			return resource.getName();
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