package rabbit.ui.internal.actions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class DropDownAction extends Action implements IMenuCreator {

  private Menu menu;
  private IAction defaultAction;
  private IAction[] menuItems;

  public DropDownAction(String text, ImageDescriptor image,
      IAction defaultAction, IAction... menuItems) {
    super(text, IAction.AS_DROP_DOWN_MENU);
    checkNotNull(defaultAction);

    setMenuCreator(this);
    setImageDescriptor(image);
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
    if (menu == null || menu.isDisposed()) {
      menu = new Menu(parent);

      for (IAction action : menuItems) {
        new ActionContributionItem(action).fill(menu, -1);
      }
    }
    return menu;
  }

  @Override
  public Menu getMenu(Menu parent) {
    return null;
  }

  protected IAction getDefaultAction() {
    return defaultAction;
  }

  protected IAction[] getMenuItemActions() {
    return menuItems;
  }
}
