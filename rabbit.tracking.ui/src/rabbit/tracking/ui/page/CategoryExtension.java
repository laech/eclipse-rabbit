package rabbit.features.ui.page;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;

import rabbit.features.ui.IPage;

public class CategoryExtension extends PageExtension {

	private SortedSet<PageExtension> pages;

	public CategoryExtension(String name, IPage page, ImageDescriptor image,
			String description) {
		
		super(name, page, image, description);

		pages = new TreeSet<PageExtension>(new Comparator<PageExtension>() {
			@Override
			public int compare(PageExtension o1, PageExtension o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public Set<PageExtension> getChildren() {
		return Collections.unmodifiableSet(pages);
	}
}
