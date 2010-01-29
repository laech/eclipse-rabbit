package rabbit.tracking.ui.pages;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;

import rabbit.tracking.ui.IPage;

public class PageExtension implements Comparable<PageExtension> {

	private IPage page;
	private SortedSet<PageExtension> pages;
	private ImageDescriptor image;
	private String description;
	private String name;

	public PageExtension(String name, IPage page, ImageDescriptor image,String description) {

		pages = new TreeSet<PageExtension>();
		
		setPage(page);
		setDescription(description);
		setImage(image);
		setName(name);
	}

	public IPage getPage() {
		return page;
	}

	public void setPage(IPage page) {
		this.page = page;
	}

	public boolean addChild(PageExtension child) {
		return pages.add(child);
	}

	public Set<PageExtension> getChildren() {
		return Collections.unmodifiableSet(pages);
	}

	public boolean removeChild(PageExtension child) {
		return pages.remove(child);
	}

	public String getDescription() {
		return description;
	}

	public ImageDescriptor getImage() {
		return image;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(ImageDescriptor img) {
		this.image = img;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(PageExtension o) {
		return getName().compareTo(o.getName());
	}
}
