package rabbit.tracking.ui.pages;

import java.util.Map;

import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.ui.DisplayPreference;
import rabbit.tracking.ui.IPage;

public class CommandPage implements IPage {
	
	private static final String[] columnNames = new String[] { "Name", "Description", "Usage Count", "Graph" };

	private TableViewer viewer;
//	private CommandEventStorer dataAccessor;

	public CommandPage() {
//		dataAccessor = new CommandEventStorer();
	}

	@Override
	public void createContents(Composite parent) {
		
		viewer = new TableViewer(parent, SWT.NONE);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		
		for (String name : columnNames) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(name);
			column.getColumn().setWidth(100);
		}
	}

	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_CUT);
	}

	@Override
	public void update(DisplayPreference p) {
//		viewer.setInput(dataAccessor.getData(p.getStartDate(), p.getEndDate()));
	}

	private static class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		private ICommandService service = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				String id = ((Map.Entry<?, ?>) element).getKey().toString();
				String name = null;
				try {
					name = service.getCommand(id).getName();
				} catch (NotDefinedException e) {
					name = id;
				}
				return name;
			}
			return null;
		}
	}
	
	private static class ContentProvider implements IStructuredContentProvider  {

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof Map<?, ?>)) {
				return null;
			}
			Map<?, ?> map = (Map<?, ?>) inputElement;
			return map.entrySet().toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
