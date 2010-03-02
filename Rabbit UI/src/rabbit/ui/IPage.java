package rabbit.ui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
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
	Image getImage();

	/**
	 * Creates the content of this page.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	void createContents(Composite parent);
	
	/**
	 * @param group TODO
	 * 
	 */
	IContributionItem[] createToolBarItems(IToolBarManager toolBar, GroupMarker group);

	/**
	 * Updates the data of this page.
	 * 
	 * @param preference
	 *            The object containing the update preferences.
	 */
	void update(DisplayPreference preference);

}
