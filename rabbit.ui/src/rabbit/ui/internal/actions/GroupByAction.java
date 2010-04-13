package rabbit.ui.internal.actions;

import rabbit.ui.internal.SharedImages;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import javax.annotation.Nonnull;

public class GroupByAction extends Action implements IMenuCreator {

  private Menu menu;
  private IAction defaultAction;
  private IAction[] menuItems;
  private ICategoryProvider categoryProvider;

  public GroupByAction(@Nonnull ICategoryProvider categoryProvider,
      @Nonnull IAction defaultAction, IAction... menuItems) {
    super("Group by " + defaultAction.getText(), IAction.AS_DROP_DOWN_MENU);
    checkNotNull(categoryProvider);
    checkNotNull(defaultAction);

    setMenuCreator(this);
    setImageDescriptor(SharedImages.HIERARCHY);
    this.categoryProvider = categoryProvider;
    this.defaultAction = defaultAction;
    this.menuItems = menuItems;
  }

  @Override
  public void run() {
    defaultAction.run();
  }

  @Override
  public void dispose() {
    if (menu != null && !menu.isDisposed()) {
      menu.dispose();
      menu = null;
    }
  }

  @Override
  public Menu getMenu(Control parent) {
    if (menu != null && !menu.isDisposed())
      return menu;

    menu = new Menu(parent);
    for (IAction action : menuItems)
      new ActionContributionItem(action).fill(menu, -1);

    if (menuItems.length > 0)
      new MenuItem(menu, SWT.SEPARATOR);

    IAction showAction = new ShowGroupByDialogAction(categoryProvider);
    new ActionContributionItem(showAction).fill(menu, -1);
    return menu;
  }

  @Override
  public Menu getMenu(Menu parent) {
    return null;
  }
}
