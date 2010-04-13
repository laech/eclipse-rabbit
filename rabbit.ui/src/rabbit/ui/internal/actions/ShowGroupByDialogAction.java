package rabbit.ui.internal.actions;

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.dialogs.GroupByDialog;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

// TODO test
public class ShowGroupByDialogAction extends Action {

  private ICategoryProvider provider;

  public ShowGroupByDialogAction(ICategoryProvider provider) {
    this("Advanced...", IAction.AS_PUSH_BUTTON, provider);
  }

  public ShowGroupByDialogAction(String text, int style,
      ICategoryProvider provider) {
    super(text, style);
    checkNotNull(provider);

    this.provider = provider;
    setImageDescriptor(SharedImages.HIERARCHY);
  }

  @Override
  public void runWithEvent(Event event) {
    Shell parentShell = event.display.getActiveShell();
    GroupByDialog dialog = new GroupByDialog(parentShell);
    dialog.create();
    dialog.getShell().setSize(400, 400);

    dialog.setSelectedCategories(provider.getSelectedCategories());
    dialog.setUnSelectedCategories(provider.getUnselectedCategories());

    if (dialog.open() == GroupByDialog.OK) {
      provider.setSelectedCategories(dialog.getSelectedCategories());
    }
  }
}
