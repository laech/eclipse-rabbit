package rabbit.ui.internal.pref;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rabbit.ui.internal.RabbitUI;

public class RabbitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	// private Button idleFeature;
	private Spinner daySpinner;

	public RabbitPreferencePage() {
	}

	public RabbitPreferencePage(String title) {
		super(title);
	}

	public RabbitPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		Composite cmp = new Composite(parent, SWT.NONE);
		cmp.setLayout(layout);
		cmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group viewGroup = new Group(cmp, SWT.NONE);
		viewGroup.setText("Rabbit View");
		viewGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		viewGroup.setLayout(new GridLayout(3, false));
		{
			new Label(viewGroup, SWT.HORIZONTAL).setText("By default, display data for the last ");
			daySpinner = new Spinner(viewGroup, SWT.BORDER);
			daySpinner.setMinimum(0);
			daySpinner.setMaximum(9999);
			daySpinner.setSelection(RabbitUI.getDefault().getDefaultDisplayDatePeriod());
			new Label(viewGroup, SWT.HORIZONTAL).setText(" days.");
		}

		// idleFeature = new Button(cmp, SWT.CHECK);
		// idleFeature.setSelection(RabbitCore.getDefault().isIdleDetectionEnabled());
		// idleFeature.setText("Enable idleness detection");
		// idleFeature.setToolTipText(
		// "Idleness detection helps to track more accurate data by taking user idleness into account");

		return cmp;
	}

	@Override
	protected void performDefaults() {
		// idleFeature.setSelection(false);
		daySpinner.setSelection(7);
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		// if (RabbitCore.getDefault().isIdleDetectionEnabled() !=
		// idleFeature.getSelection()) {
		// RabbitCore.getDefault().setIdleDetectionEnabled(idleFeature.getSelection());
		// }
		if (RabbitUI.getDefault().getDefaultDisplayDatePeriod() != daySpinner.getSelection()) {
			RabbitUI.getDefault().setDefaultDisplayDatePeriod(daySpinner.getSelection());
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
