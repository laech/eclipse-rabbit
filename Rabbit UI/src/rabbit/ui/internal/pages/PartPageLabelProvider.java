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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;

import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

/**
 * Label provider for a part page.
 */
public class PartPageLabelProvider extends BaseLabelProvider implements ITableLabelProvider,
		IColorProvider {

	/** Keys are workbench part id, values may be null. */
	private Map<String, Image> images;
	private PartPage page;
	private final Color undefinedColor;

	/**
	 * Constructs a new page.
	 * 
	 * @param page
	 *            The parent.
	 */
	public PartPageLabelProvider(PartPage page) {
		this.page = page;
		images = new HashMap<String, Image>();
		undefinedColor = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	@Override
	public void dispose() {
		for (Image img : images.values()) {
			if (img != null) {
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
		if ((columnIndex != 0) || !(element instanceof IWorkbenchPartDescriptor)) {
			return null;
		}

		IWorkbenchPartDescriptor part = (IWorkbenchPartDescriptor) element;
		if (images.containsKey(part.getId())) {
			return images.get(part.getId());

		} else {
			ImageDescriptor des = part.getImageDescriptor();
			Image img = (des != null) ? des.createImage() : null;
			images.put(part.getId(), img);
			return img;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof IWorkbenchPartDescriptor)) {
			return null;
		}
		IWorkbenchPartDescriptor part = (IWorkbenchPartDescriptor) element;
		switch (columnIndex) {
		case 0:
			return part.getLabel();
		case 1:
			return MillisConverter.toDefaultString(page.getValue(part));
		default:
			return null;
		}
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof UndefinedWorkbenchPartDescriptor) {
			return undefinedColor;
		}
		return null;
	}
}
