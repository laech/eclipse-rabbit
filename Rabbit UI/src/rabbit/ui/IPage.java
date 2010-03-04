package rabbit.ui;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;


/**
 * Represents a page for displaying graphical information.
 */
public interface IPage {

	/**
	 * Creates the content of this page.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	void createContents(Composite parent);
	
	/**
	 * 
	 */
	IContributionItem[] createToolBarItems(IToolBarManager toolBar);

	/**
	 * Updates the data of this page.
	 * 
	 * @param preference
	 *            The object containing the update preferences.
	 */
	void update(DisplayPreference preference);

}
