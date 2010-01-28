package rabbit.features.ui.page;

import org.eclipse.jface.resource.ImageDescriptor;

import rabbit.features.ui.IPage;

public class PageExtension {

	private ImageDescriptor image;
	private String description;
	private String name;
	private IPage page;

	public PageExtension(String name, IPage page, ImageDescriptor image,
			String description) {

		setDescription(description);
		setImage(image);
		setName(name);
		setPage(page);
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

	public IPage getPage() {
		return page;
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

	public void setPage(IPage page) {
		this.page = page;
	}
}
