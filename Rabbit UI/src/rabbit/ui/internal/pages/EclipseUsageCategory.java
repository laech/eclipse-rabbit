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

/**
 * An empty category page used to contain other pages.
 */
public class EclipseUsageCategory implements IPage {

	public EclipseUsageCategory() {
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		Label label = new Label(parent, SWT.NONE);
		label.setBackground(parent.getBackground());
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		label.setText("This category contains pages that display usage information about Eclipse.");
	}

	@Override
	public void update(DisplayPreference preference) {
	}

	@Override
	public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
		return new IContributionItem[0];
	}

}
