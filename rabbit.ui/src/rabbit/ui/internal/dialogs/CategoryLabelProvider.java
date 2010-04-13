package rabbit.ui.internal.dialogs;

import rabbit.ui.internal.actions.ICategory;

import com.google.common.collect.Maps;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import java.util.Map;

// TODO test
public class CategoryLabelProvider extends LabelProvider {

  private Map<ICategory, Image> images;
  
  public CategoryLabelProvider() {
    images = Maps.newHashMap();
  }
  
  @Override
  public void dispose() {
    super.dispose();
    for (Image img : images.values()) {
      if (img != null) {
        img.dispose();
      }
    }
  }
  
  @Override
  public String getText(Object element) {
    return ((ICategory) element).getText();
  }
  
  @Override
  public Image getImage(Object element) {
    if (images.containsKey(element)) {
      return images.get(element);
    }
    
    ICategory category = (ICategory) element;
    ImageDescriptor des = category.getImageDescriptor();
    Image image = (des != null) ? des.createImage() : null;
    images.put(category, image);
    return image;
  }
}
