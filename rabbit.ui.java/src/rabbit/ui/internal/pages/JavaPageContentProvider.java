/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.pages;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CUNIT;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKFRAG_ROOT;
import static org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;

// TODO
@SuppressWarnings("restriction")
public class JavaPageContentProvider extends AbstractValueContentProvider {
  
  /*
   * This content provider will only show the following elements inside a java
   * class type:
   * 
   * Java types, and methods.
   * 
   * Elements such as fields, imports, anonymous inner classes are hidden away.
   * For a number of reasons, for example: imports are not that useful to see;
   * anonymous classes do not have unique identifiers; the concept of "time 
   * spent working on a field" is very blur.
   * 
   * Hiding those elements in the viewer will NOT affect the duration of parent 
   * elements or any other elements, because the hidden elements are still in 
   * the tree mode, we still use those to calculate duration for parent elements.
   */
  
  /**
   * Categories supported by this content provider.
   */
  public static enum JavaCategory implements ICategory {
    
    DATE        ("Dates", SharedImages.CALENDAR),
    PROJECT     ("Projects", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),
    PACKAGE_ROOT("Source Folders", images.getImageDescriptor(IMG_OBJS_PACKFRAG_ROOT)),
    PACKAGE     ("Packages", images.getImageDescriptor(IMG_OBJS_PACKAGE)),
    TYPE_ROOT   ("Files", images.getImageDescriptor(IMG_OBJS_CUNIT)),
    TYPE        ("Types", images.getImageDescriptor(IMG_OBJS_CLASS)),
    METHOD      ("Methods", images.getImageDescriptor(IMG_OBJS_PUBLIC)),
    ;
    
    private String text;
    private ImageDescriptor image;
    
    private JavaCategory(String text, ImageDescriptor image) {
      this.text = text;
      this.image = image;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return image;
    }

    @Override
    public String getText() {
      return text;
    }
  }
  
  private static final ISharedImages images = JavaUI.getSharedImages();

  /**
   * Constructs a new content provider for the given viewer.
   * @param treeViewer The viewer.
   * @throws NullPointerException If the viewer is null.
   */
  public JavaPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
  }
  
  private boolean isInnerType(IJavaElement type) {
    return type.getElementType() == IJavaElement.TYPE
        && type.getParent().getElementType() == IJavaElement.TYPE;
  }
  
  private boolean isAnoynmousType(IJavaElement type) {
    if (type.getElementType() == IJavaElement.TYPE) {
      return type.getParent().getElementType() == IJavaElement.METHOD;
    }
    return false;
  }
  
  private boolean isField(IJavaElement element) {
    return element.getElementType() == IJavaElement.FIELD;
  }
  
  @Override
  public boolean hasChildren(Object element) {
    if (!(element instanceof TreeNode)) {
      return false;
    }
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null) {
      return false;
    }
    
    // If all children are of type java.lang.Long, then false:
    for (TreeNode child : node.getChildren()) {
      if (!(child.getValue() instanceof Long)) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);

    Collection<JavaDataDescriptor> data = (Collection<JavaDataDescriptor>) newInput;
    for (JavaDataDescriptor des : data) {

      IJavaElement element = des.findElement();
      if (element == null) {
        continue;
      }

      TreeNode node = getRoot();
      List<IJavaElement> elements = getHierarchy(element);
      for (ICategory cat : selectedCategories) {

        if (JavaCategory.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
        } else {
          // Else for other JavaCategory:
          Predicate<Object> categorizer = getCategorizers().get(cat);
          if (categorizer == null) {
            continue; // Invalid category.
          }

          for (IJavaElement e : elements) {
            if (isAnoynmousType(e) || isField(e) || isInnerType(e)) {
              break; // We don't want to show any of these and their children.
            }
            if (categorizer.apply(e)) {
              node = TreeNodes.findOrAppend(node, e);
              break;
            }
          }
        }
      }
      TreeNodes.appendToParent(node, des.getValue());
    }
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return JavaCategory.values();
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return JavaCategory.METHOD;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { 
        JavaCategory.PROJECT,
        JavaCategory.PACKAGE_ROOT,
        JavaCategory.PACKAGE,
        JavaCategory.TYPE_ROOT,
        JavaCategory.TYPE,
        JavaCategory.METHOD,
    };
  }
  
  @Override
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableMap.<ICategory, Predicate<Object>> builder()
        .put(JavaCategory.DATE,         instanceOf(LocalDate.class))
        .put(JavaCategory.PROJECT,      instanceOf(IJavaProject.class))
        .put(JavaCategory.PACKAGE_ROOT, instanceOf(IPackageFragmentRoot.class))
        .put(JavaCategory.PACKAGE,      instanceOf(IPackageFragment.class))
        .put(JavaCategory.TYPE_ROOT,    instanceOf(ITypeRoot.class))
        .put(JavaCategory.TYPE,         instanceOf(IType.class))
        .put(JavaCategory.METHOD,       instanceOf(IMethod.class))
        .build();
  }
  
  /**
   * Gets the hierarchy of from the element to the project.
   * 
   * @param element The element.
   * @return An ordered list of elements, the first element is the highest
   *         parent, the last element is the argument itself.
   */
  private List<IJavaElement> getHierarchy(IJavaElement element) {
    List<IJavaElement> elements = Lists.newArrayList();
    elements.add(element);
    while ((element = element.getParent()) != null) {
      elements.add(0, element);
    }
    if (elements.get(0) instanceof IJavaModel) {
      elements.remove(0);
    }
    return elements;
  }
}
