package rabbit.ui.internal.actions;

import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.dialogs.FilteredTree;

// TODO
public class FilteredTreeAction extends Action {

  private final FilteredTree fTree;

  public FilteredTreeAction(FilteredTree tree) {
    super("Search", IAction.AS_CHECK_BOX);
    setImageDescriptor(SharedImages.SEARCH);
    setChecked(tree.getFilterControl().getParent().isVisible());
    fTree = tree;
  }

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
    setChecked(fTree.getFilterControl().getParent().isVisible());
  }
}
