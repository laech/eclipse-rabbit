package rabbit.tracking.ui.pages.internal;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for a command page.
 */
public class CommandPageLabelProvider extends LabelProvider implements ITableLabelProvider {

	private CommandPage page;

	/**
	 * Constructs a new label provider.
	 * 
	 * @param page The parent.
	 */
	public CommandPageLabelProvider(CommandPage page) {
		this.page = page;
	}

	@Override public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof Command)) {
			return null;
		}

		Command cmd = (Command) element;
		try {
			switch (columnIndex) {
			case 0:
				return cmd.getName();
			case 1:
				return cmd.getDescription();
			case 2:
				return Long.toString(page.getUsage(cmd));
			default:
				return null;
			}
		} catch (NotDefinedException e) {
			return (columnIndex == 0) ? cmd.getId() : null;
		}
	}
}
