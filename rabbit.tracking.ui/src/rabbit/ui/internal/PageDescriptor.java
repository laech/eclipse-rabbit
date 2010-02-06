package rabbit.ui.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import rabbit.ui.pages.IPage;

/**
 * Represents a page extension descriptor.
 */
public class PageDescriptor {

	private IPage page;
	private SortedSet<PageDescriptor> pages;
	private String description;
	private String name;

	/**
	 * Constructs a new descriptor.
	 * 
	 * @param name
	 *            The name.
	 * @param page
	 *            The actual page.
	 * @param description
	 *            The description.
	 * @throws NullPointerException
	 *             If name or page is null.
	 */
	public PageDescriptor(String name, IPage page, String description) {
		// According to the extension point schema:
		if (name == null || page == null)
			throw new NullPointerException();

		pages = new TreeSet<PageDescriptor>(new Comparator<PageDescriptor>() {
			@Override
			public int compare(PageDescriptor o1, PageDescriptor o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		this.page = page;
		this.name = name;
		this.description = description;
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
	 * Adds a child page.
	 * 
	 * @param child
	 *            The child page.
	 * @return true if the page is successfully added, false if an identical
	 *         page already added.
	 */
	public boolean addChild(PageDescriptor child) {
		if (child == this)
			return false;
		else
			return pages.add(child);
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
	 * Removes a child page.
	 * 
	 * @param child
	 *            The page to be removed.
	 * @return true if the page is removed, false if there is no such page.
	 */
	public boolean removeChild(PageDescriptor child) {
		return pages.remove(child);
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
	 * Gets the name of the page.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
}
