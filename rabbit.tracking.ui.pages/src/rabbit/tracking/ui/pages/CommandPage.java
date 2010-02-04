package rabbit.tracking.ui.pages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.storage.xml.CommandDataAccessor;
import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.ui.DisplayPreference;

public class CommandPage extends AbstractGraphicalTablePage {
	
	private static ICommandService service = (ICommandService) PlatformUI
			.getWorkbench().getService(ICommandService.class);;

	private IAccessor dataStore;

	
	private Map<Command, Long> dataMapping;
	
	public CommandPage() {
		super();
		dataStore = new CommandDataAccessor();
		dataMapping = new HashMap<Command, Long>();
	}
	
	long getUsage(Command cmd) {
		Long usage = dataMapping.get(cmd);
		if (usage == null) {
			return 0;
		} else {
			return usage.longValue();
		}
	}
	
	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_CUT);
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxUsage(0);
		dataMapping.clear();
		
		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Entry<String, Long> entry : data.entrySet()) {
			dataMapping.put(service.getCommand(entry.getKey()), entry.getValue());
			
			if (entry.getValue() > getMaxUsage()) {
				setMaxUsage(entry.getValue());
			}
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override
	protected TableColumn[] createColumns(Table table) {
		TableColumn nameCol = new TableColumn(table, SWT.LEFT);
		nameCol.setText("Name");
		nameCol.setWidth(150);
		nameCol.setMoveable(true);
		
		TableColumn descriptionCol = new TableColumn(table, SWT.LEFT);
		descriptionCol.setText("Description");
		descriptionCol.setWidth(200);
		descriptionCol.setMoveable(true);

		return new TableColumn[] { nameCol, descriptionCol };
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new CommandPageContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new CommandPageLabelProvider(this);
	}

	@Override
	protected String getUsageColumnText() {
		return "Usage Count";
	}
}
