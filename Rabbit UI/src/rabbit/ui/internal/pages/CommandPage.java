package rabbit.ui.internal.pages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.CommandDataAccessor;
import rabbit.ui.DisplayPreference;

/**
 * A page for displaying command usage.
 */
public class CommandPage extends AbstractGraphTreePage {

	private IAccessor accessor;
	private ICommandService service;

	/** The data model, the values are usage counts of the commands. */
	private Map<Command, Long> dataMapping;

	/**
	 * Constructor.
	 */
	public CommandPage() {
		super();
		accessor = new CommandDataAccessor();
		dataMapping = new HashMap<Command, Long>();
		service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
	}

	@Override
	protected TreeColumn[] createColumns(Tree table) {
		TreeColumn nameCol = new TreeColumn(table, SWT.LEFT);
		nameCol.setText("Name");
		nameCol.setWidth(150);
		nameCol.setMoveable(true);

		TreeColumn descriptionCol = new TreeColumn(table, SWT.LEFT);
		descriptionCol.setText("Description");
		descriptionCol.setWidth(200);
		descriptionCol.setMoveable(true);

		return new TreeColumn[] { nameCol, descriptionCol };
	}

	@Override
	protected ITreeContentProvider createContentProvider() {
		return new CollectionContentProvider();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new CommandPageLabelProvider(this);
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_CUT);
	}

	@Override
	public long getValue(Object o) {
		Long value = dataMapping.get(o);
		return (value == null) ? 0 : value;
	}

	@Override
	protected String getValueColumnText() {
		return "Usage Count";
	}

	@Override
	public void update(DisplayPreference p) {
		setMaxValue(0);
		dataMapping.clear();

		Map<String, Long> data = accessor.getData(p.getStartDate(), p.getEndDate());
		for (Entry<String, Long> entry : data.entrySet()) {
			dataMapping.put(service.getCommand(entry.getKey()), entry.getValue());

			if (entry.getValue() > getMaxValue()) {
				setMaxValue(entry.getValue());
			}
		}
		getViewer().setInput(dataMapping.keySet());
	}
}
