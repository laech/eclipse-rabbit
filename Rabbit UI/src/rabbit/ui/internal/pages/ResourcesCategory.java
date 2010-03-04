package rabbit.ui.internal.pages;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

import rabbit.ui.DisplayPreference;
import rabbit.ui.IPage;

public class ResourcesCategory implements IPage {

	public ResourcesCategory() {
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
