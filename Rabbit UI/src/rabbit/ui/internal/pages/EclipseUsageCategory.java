package rabbit.ui.internal.pages;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

import rabbit.ui.DisplayPreference;
import rabbit.ui.IPage;

/**
 * An empty category page used to contain other pages.
 */
public class EclipseUsageCategory implements IPage {

	public EclipseUsageCategory() {
	}

	@Override
	public void createContents(Composite parent) {
	}

	@Override
	public void update(DisplayPreference preference) {
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		return new IContributionItem[0];
	}

}
