package rabbit.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;


/**
 * Represents a page for displaying graphical information.
 */
public interface IPage {

	/**
	 * Gets a 16x16 image icon for this page, the image must be disposed by the
	 * creator.
	 * 
	 * @return The image icon, or null.
	 */
	public Image getImage();

	/**
	 * Creates the content of this page.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public void createContents(Composite parent);

	/**
	 * Updates the data of this page.
	 * 
	 * @param preference
	 *            The object containing the update preferences.
	 */
	public void update(DisplayPreference preference);

}
