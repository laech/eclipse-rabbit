package rabbit.ui.internal.pages;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rabbit.ui.DisplayPreference;
import rabbit.ui.IPage;

public class ResourcesCategory implements IPage {

	public ResourcesCategory() {
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		Label label = new Label(parent, SWT.NONE);
		label.setBackground(parent.getBackground());
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		label.setText("This category contains pages that display information relating to projects.");
	}

	@Override
	public void update(DisplayPreference preference) {
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		return new IContributionItem[0];
	}

}
