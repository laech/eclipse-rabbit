package rabbit.tracking.ui.pages.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.storage.xml.CommandDataAccessor;
import rabbit.tracking.storage.xml.IAccessor;
import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.pages.AbstractGraphTablePage;

/**
 * A page for displaying command usage.
 */
public class CommandPage extends AbstractGraphTablePage {

	private IAccessor dataStore;
	private ICommandService service;

	/** The data model, the values are usage counts of the commands. */
	private Map<Command, Long> dataMapping;

	/**
	 * Constructor.
	 */
	public CommandPage() {
		super();
		dataStore = new CommandDataAccessor();
		dataMapping = new HashMap<Command, Long>();
		service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
	}

	/**
	 * Gets the usage of a command.
	 * 
	 * @param cmd The command.
	 * @return The usage count of the command.
	 */
	long getUsage(Command cmd) {
		Long usage = dataMapping.get(cmd);
		if (usage == null) {
			return 0;
		} else {
			return usage.longValue();
		}
	}

	@Override public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_CUT);
	}

	@Override public void update(DisplayPreference p) {
		setMaxValue(0);
		dataMapping.clear();

		Map<String, Long> data = dataStore.getData(p.getStartDate(), p.getEndDate());
		for (Entry<String, Long> entry : data.entrySet()) {
			dataMapping.put(service.getCommand(entry.getKey()), entry.getValue());

			if (entry.getValue() > getMaxValue()) {
				setMaxValue(entry.getValue());
			}
		}
		getViewer().setInput(dataMapping.keySet());
	}

	@Override protected TableColumn[] createColumns(Table table) {
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

	@Override protected IStructuredContentProvider createContentProvider() {
		return new CommandPageContentProvider();
	}

	@Override protected ITableLabelProvider createLabelProvider() {
		return new CommandPageLabelProvider(this);
	}

	@Override protected String getValueColumnText() {
		return "Usage Count";
	}
}
