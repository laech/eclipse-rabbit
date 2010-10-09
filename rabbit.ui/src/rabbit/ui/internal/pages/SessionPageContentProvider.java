package rabbit.ui.internal.pages;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Collections;

// TODO
class SessionPageContentProvider extends AbstractValueContentProvider {

  /**
   * Provides objects of type {@link SessionDataDescriptor}.
   */
  static interface IProvider extends rabbit.ui.IProvider<SessionDataDescriptor> {
  }
  
  SessionPageContentProvider(TreeViewer viewer) {
    super(viewer);
  }

  @Override 
  protected ICategory[] getAllSupportedCategories() {
    return new ICategory[] { Category.DATE, Category.WORKSPACE };
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.DATE;
  }

  @Override 
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.DATE, Category.WORKSPACE };
  }

  @Override 
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return new ImmutableMap.Builder<ICategory, Predicate<Object>>()
        .put(Category.DATE, instanceOf(LocalDate.class))
        .put(Category.WORKSPACE, instanceOf(WorkspaceStorage.class))
        .build();
  }

  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    Collection<SessionDataDescriptor> data = Collections.emptySet();
    if (newInput instanceof SessionPageContentProvider.IProvider) {
      data = ((SessionPageContentProvider.IProvider) newInput).get();
    }
    
    for (SessionDataDescriptor des : data) {
      TreeNode node = getRoot();
      for (ICategory cat : getSelectedCategories()) {
        
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.WORKSPACE == cat) {
        }
      }
      TreeNodes.appendToParent(node, des.getDuration().getMillis(), true);
    }
  }
}
