package rabbit.ui.internal.pages;

import rabbit.ui.IPage;
import rabbit.ui.Preferences;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class Z implements IPage {

  private FilteredTree fTree;

  @Override
  public void createContents(Composite parent) {
    GridLayoutFactory.fillDefaults().margins(1, 1).applyTo(parent);
    fTree = new FilteredTree(parent, SWT.BORDER, new PatternFilter(), false);
    fTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    GridLayout gl = (GridLayout) fTree.getLayout();
    gl.verticalSpacing = 0;

    TreeColumn column = new TreeColumn(fTree.getViewer().getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(500);

    fTree.getViewer().getTree().setHeaderVisible(true);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IAction a = new Action("i", RabbitUI.imageDescriptorFromPlugin(
        RabbitUI.PLUGIN_ID, "/icons/full/obj16/filter.gif")) {
      @Override
      public void run() {
        GridData data = (GridData) fTree.getFilterControl().getParent()
            .getLayoutData();
        if (data.heightHint == 0) {
          data.heightHint = SWT.DEFAULT;
          fTree.getFilterControl().getParent().setVisible(true);
        } else {
          data.heightHint = 0;
          fTree.getFilterControl().getParent().setVisible(false);
        }

        fTree.getFilterControl().getParent().getParent().layout();
      }
    };

    ActionContributionItem i = new ActionContributionItem(a);
    toolBar.add(i);

    return new IContributionItem[] { i };
  }

  @Override
  public void update(Preferences preference) {
    // TODO Auto-generated method stub

  }

}
