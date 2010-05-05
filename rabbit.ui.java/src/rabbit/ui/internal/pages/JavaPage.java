package rabbit.ui.internal.pages;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.pages.JavaPageContentProvider.JavaCategory;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.CellPainter;

import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;

// TODO
@SuppressWarnings("restriction")
public class JavaPage extends AbstractAccessorPage {
  
  
  private final IAccessor<JavaDataDescriptor> accessor;
  private JavaPageContentProvider contentProvider;
  private JavaPageLabelProvider labelProvider;
  
  public JavaPage() {
    accessor = DataHandler.getAccessor(JavaDataDescriptor.class);
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contentProvider);
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setWidth(200);
    column.setText("Name");
    // TOOD
    
//    TreeViewerColumn vc = new TreeViewerColumn(viewer, SWT.LEFT);
//    vc.getColumn().setWidth(200);
//    vc.getColumn().setText("Name");
//    vc.setLabelProvider(new DecoratingStyledCellLabelProvider(labelProvider, null, null));
    
    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setWidth(100);
    column.setText("Time Spent");
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected ViewerComparator createInitialComparator(TreeViewer viewer) {
    return new JavaElementComparator() {
      
      @Override
      public int category(Object element) {
        if (element instanceof TreeNode) {
          element = ((TreeNode) element).getValue();
        }
        return super.category(element);
      }
      
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof TreeNode) {
          e1 = ((TreeNode) e1).getValue();
        }
        if (e2 instanceof TreeNode) {
          e2 = ((TreeNode) e2).getValue();
        }
        return super.compare(viewer, e1, e2);
      }
    };
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contentProvider = new JavaPageContentProvider(viewer);
    labelProvider = new JavaPageLabelProvider(contentProvider);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filter = new ShowHideFilterControlAction(getFilteredTree());
    filter.run();
    
    IAction collapse = new CollapseAllAction(getViewer());
    
    ICategory cat = JavaCategory.PACKAGE;
    IAction groupByPkg = new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override public void run() {
        contentProvider.setSelectedCategories(JavaCategory.PACKAGE, 
            JavaCategory.TYPE_ROOT, JavaCategory.TYPE);
      }
    };
    
    cat = JavaCategory.PROJECT;
    IAction groupByPrj = new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override public void run() {
        contentProvider.setSelectedCategories(JavaCategory.PROJECT,
            JavaCategory.PACKAGE_ROOT, JavaCategory.PACKAGE, 
            JavaCategory.TYPE_ROOT, JavaCategory.TYPE, JavaCategory.METHOD);
      }
    };
    
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filter),
        new Separator(),
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contentProvider, new Action(){})),
    };
    
    for (IContributionItem item : items) {
      toolBar.add(item);
    }
    
    return items;
  }

  @Override
  protected IAccessor<?> getAccessor() {
    return accessor;
  }
}
