package rabbit.ui.internal.pages;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;

import rabbit.ui.IPage;

public abstract class AbstractPage implements IPage {

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar, GroupMarker group) {
		return new IContributionItem[0];
	}
}
