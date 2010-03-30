package rabbit.ui.internal.pages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import rabbit.core.storage.LaunchDescriptor;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.LaunchResource;
import rabbit.ui.internal.util.MillisConverter;

public class LaunchPageLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	private final DateFormat format;
	
	protected final Image runImg;
	protected final Image debugImg;
	protected final Image profileImg;
	protected final ResourcePageLabelProvider provider;
	protected final ILaunchConfigurationType[] configs;

	public LaunchPageLabelProvider() {
		format = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
		runImg = SharedImages.RUN.createImage();
		debugImg = SharedImages.DEBUG.createImage();
		profileImg = SharedImages.PROFILE.createImage();
		provider = new ResourcePageLabelProvider();
		
		configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationTypes();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		runImg.dispose();
		debugImg.dispose();
		profileImg.dispose();
		provider.dispose();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex != 0) {
			return null;
		}
		
		if (element instanceof LaunchDescriptor) {
//			switch (((LaunchDescriptor) element).getLaunchModeId()) {
//			case RUN_MODE:
//				return runImg;
//			case DEBUG_MODE:
//				return debugImg;
//			case PROFILE_MODE:
//				return profileImg;
//			default:
//				return null;
//			}

			for (ILaunchConfigurationType type : configs) {
				if (type.getName().equals(((LaunchDescriptor) element).getLaunchTypeId())) {
				}
			}
			
		} else if (element instanceof LaunchResource) {
			return provider.getImage(((LaunchResource) element).getResource());
		}
		
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		switch (columnIndex) {
		case 0:
			if (element instanceof LaunchDescriptor) {
				return ((LaunchDescriptor) element).getLaunchName();

			} else if (element instanceof LaunchResource) {
				return provider.getText(((LaunchResource) element).getResource());
			}
			break;
			
		case 1:
			if (element instanceof LaunchDescriptor) {
				return ((LaunchDescriptor) element).getLaunchTypeId();
			}
			break;

		case 2:
			if (element instanceof LaunchDescriptor) {
				return ((LaunchDescriptor) element).getLaunchModeId().toString();
			}
			break;

		case 3:
			if (element instanceof LaunchDescriptor) {
				Calendar time = ((LaunchDescriptor) element).getLaunchTime();
				return format.format(time.getTime());
			}
			break;

		case 4:
			if (element instanceof LaunchDescriptor) {
				return MillisConverter.toDefaultString(
						((LaunchDescriptor) element).getDuration());
			}
			break;
		}
		return null;
	}

}
