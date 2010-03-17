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
package rabbit.ui.internal.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;

import rabbit.ui.IPage;

/**
 * Represents a page extension descriptor.
 */
public class PageDescriptor {

	public final String parentId;
	public final String id;
	public final String name;
	public final String description;
	public final IPage page;
	public final ImageDescriptor image;
	public final Set<PageDescriptor> pages;

	public PageDescriptor(String id, String name, IPage page,
			String description, ImageDescriptor image, String parentId) {
		this.id = id;
		this.parentId = parentId;
		this.page = page;
		this.name = name;
		this.image = image;
		this.description = description;
		pages = new HashSet<PageDescriptor>();
	}

	@Override
	public String toString() {
		return name + ": " + id;
	}
}
