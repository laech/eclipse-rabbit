package rabbit.ui.internal.actions;

import rabbit.ui.internal.SharedImages;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class GroupByAction extends Action implements IMenuCreator {

  private Menu menu;
  private ICategoryProvider provider;

  public GroupByAction(ICategoryProvider categoryProvider) {
    super("Group by", IAction.AS_DROP_DOWN_MENU);
    checkNotNull(categoryProvider);
    setMenuCreator(this);
    setImageDescriptor(SharedImages.HIERARCHY);
    provider = categoryProvider;
  }

  @Override
  public void dispose() {
    if (menu != null && !menu.isDisposed()) {
      menu.dispose();
    }
  }

  @Override
  public Menu getMenu(Control parent) {
    if (menu == null) {
      menu = new Menu(parent);

      for (ICategory cat : provider.getCategories()) {
        IAction action = new Action(cat.getName(), cat.getImageDescriptor()) {
          @Override
          public void run() {
//            provider.setCategories(categories)
          }
        };
      }
    }
    return menu;
  }

  @Override
  public Menu getMenu(Menu parent) {
    return null;
  }

}
