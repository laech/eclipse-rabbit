package rabbit.tracking.ui.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.storage.xml.CommandEventStorer;
import rabbit.tracking.ui.DisplayPreference;

public class CommandPage extends AbstractGraphTablePage {
	
	private static ICommandService service = (ICommandService) PlatformUI
			.getWorkbench().getService(ICommandService.class);;

	private TableViewer viewer;
	private TableColumn nameCol;
	private TableColumn usageCol;
	private TableColumn graphCol;
	private TableColumn descriptionCol;
	
	public static final int NAME_COLUMN_INDEX = 0;
	public static final int DESCRIPTION_COLUMN_INDEX = 1;
	public static final int USAGE_COLUMN_INDEX = 2;
	public static final int GRAPH_COLUMN_INDEX = 3;

	private CommandEventStorer dataStore;

	
	private Map<Command, Integer> dataMapping;
	
	public CommandPage() {
		super();
		dataStore = new CommandEventStorer();
		dataMapping = new HashMap<Command, Integer>();
	}

	@Override
	public void doCreateContents(Composite parent) {

		viewer = new TableViewer(parent, SWT.NONE);
		viewer.setContentProvider(new CommandPageContentProvider());
		viewer.setLabelProvider(new CommandPageLabelProvider(this));

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		
		nameCol = new TableColumn(table, SWT.LEFT, NAME_COLUMN_INDEX);
		nameCol.setText("Name");
		nameCol.setWidth(120);
		nameCol.setMoveable(true);
		nameCol.addSelectionListener(new TableColumnComparator(viewer) {
			@Override
			protected int getColumnIndex() {
				return NAME_COLUMN_INDEX;
			}
		});
		
		descriptionCol = new TableColumn(table, SWT.LEFT, DESCRIPTION_COLUMN_INDEX);
		descriptionCol.setText("Description");
		descriptionCol.setWidth(180);
		descriptionCol.setMoveable(true);
		descriptionCol.addSelectionListener(new TableColumnComparator(viewer) {
			@Override
			protected int getColumnIndex() {
				return DESCRIPTION_COLUMN_INDEX;
			}
		});
		
		usageCol = new TableColumn(table, SWT.RIGHT, USAGE_COLUMN_INDEX);
		usageCol.setText("Usage");
		usageCol.setWidth(60);
		usageCol.setMoveable(true);
		usageCol.addSelectionListener(new TableColumnComparator(viewer) {
			@Override
			protected int getColumnIndex() {
				return USAGE_COLUMN_INDEX;
			}
		});
		
		graphCol = new TableColumn(table, SWT.LEFT, GRAPH_COLUMN_INDEX);
		graphCol.setWidth(100);
		graphCol.addSelectionListener(new TableColumnComparator(viewer) {
			@Override
			protected int getColumnIndex() {
				return USAGE_COLUMN_INDEX; // Same as sorting the usage column.
			}
		});
	}
	
	int getUsage(Command cmd) {
		Integer usage = dataMapping.get(cmd);
		if (usage == null) {
			return 0;
		} else {
			return usage.intValue();
		}
	}
	
	@Override
	protected TableViewer getViewer() {
		return viewer;
	}

	@Override
	protected TableColumn getGraphColumn() {
		return graphCol;
	}
	
	@Override
	protected TableColumn getUsageColumn() {
		return usageCol;
	}
	
	@Override
	protected TableColumn[] getAllColumns() {
		return new TableColumn[] { nameCol, descriptionCol, usageCol, graphCol };
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
		
		Map<String, Integer> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Map.Entry<String, Integer> entry : data.entrySet()) {
			dataMapping.put(service.getCommand(entry.getKey()), entry.getValue());
			
			if (entry.getValue() > getMaxUsage()) {
				setMaxUsage(entry.getValue());
			}
		}
		viewer.setInput(dataMapping.keySet());
	}
}
