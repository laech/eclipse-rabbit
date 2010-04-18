package rabbit.ui.internal.util;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

public class TreeNodeDecorator implements ILabelDecorator {

  private ILabelDecorator decorator;
  
  public TreeNodeDecorator() {
    this.decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
  }

  @Override
  public Image decorateImage(Image image, Object element) {
    return decorator.decorateImage(image, ((TreeNode) element).getValue());
  }

  @Override
  public String decorateText(String text, Object element) {
    return decorator.decorateText(text, ((TreeNode) element).getValue());
  }

  @Override
  public void addListener(ILabelProviderListener listener) {
    decorator.addListener(listener);
  }

  @Override
  public void dispose() {
    decorator.dispose();
  }

  @Override
  public boolean isLabelProperty(Object element, String property) {
    return decorator.isLabelProperty(((TreeNode) element).getValue(), property);
  }

  @Override
  public void removeListener(ILabelProviderListener listener) {
    decorator.removeListener(listener);
  }

}
