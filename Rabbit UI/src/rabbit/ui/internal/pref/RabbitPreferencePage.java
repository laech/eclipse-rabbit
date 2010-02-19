package rabbit.ui.internal.pref;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rabbit.core.RabbitCore;

public class RabbitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button idleFeature;

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

		idleFeature = new Button(cmp, SWT.CHECK);
		idleFeature.setSelection(RabbitCore.getDefault().isIdleDetectionEnabled());
		idleFeature.setText("Enable idleness detection");
		idleFeature.setToolTipText(
				"Idleness detection helps to track more accurate data by taking user idleness into account");

		return cmp;
	}

	@Override
	protected void performDefaults() {
		idleFeature.setSelection(false);
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		if (RabbitCore.getDefault().isIdleDetectionEnabled() != idleFeature.getSelection()) {
			RabbitCore.getDefault().setIdleDetectionEnabled(idleFeature.getSelection());
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}
