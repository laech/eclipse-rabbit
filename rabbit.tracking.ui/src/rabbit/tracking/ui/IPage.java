package rabbit.tracking.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

public interface IPage {
	
	public ImageDescriptor getIcon();
	
	public void createContents(Composite parent);
	
	public void update(DisplayPreference preference);
	
}
