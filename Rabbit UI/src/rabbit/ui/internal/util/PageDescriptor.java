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

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;

import rabbit.ui.IPage;

/**
 * Represents a page extension descriptor.
 */
public class PageDescriptor {

	private IPage page;
	private SortedSet<PageDescriptor> pages;
	private String description;
	private String name;
	private ImageDescriptor image;

	/**
	 * Constructs a new descriptor.
	 * 
	 * @param name
	 *            The name.
	 * @param page
	 *            The actual page.
	 * @param description
	 *            The description.
	 * @param image
	 *            The image icon.
	 * @throws NullPointerException
	 *             If name or page is null.
	 */
	public PageDescriptor(String name, IPage page, String description, ImageDescriptor image) {
		// According to the extension point schema:
		if (name == null || page == null) {
			throw new NullPointerException();
		}

		pages = new TreeSet<PageDescriptor>(new Comparator<PageDescriptor>() {
			@Override
			public int compare(PageDescriptor o1, PageDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		this.page = page;
		this.name = name;
		this.image = image;
		this.description = description;
	}

	/**
	 * Adds a child page.
	 * 
	 * @param child
	 *            The child page.
	 * @return true if the page is successfully added, false if an identical
	 *         page already added.
	 */
	public boolean addChild(PageDescriptor child) {
		if (child == this) {
			return false;
		} else {
			return pages.add(child);
		}
	}

	/**
	 * Gets the child pages.
	 * 
	 * @return The child pages.
	 */
	public Set<PageDescriptor> getChildren() {
		return Collections.unmodifiableSet(pages);
	}

	/**
	 * Gets the description of the page.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the image descriptor of the page.
	 * 
	 * @return The image descriptor.
	 */
	public ImageDescriptor getImageDescriptor() {
		return image;
	}

	/**
	 * Gets the name of the page.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the page.
	 * 
	 * @return The page.
	 */
	public IPage getPage() {
		return page;
	}

	/**
	 * Removes a child page.
	 * 
	 * @param child
	 *            The page to be removed.
	 * @return true if the page is removed, false if there is no such page.
	 */
	public boolean removeChild(PageDescriptor child) {
		return pages.remove(child);
	}
}
