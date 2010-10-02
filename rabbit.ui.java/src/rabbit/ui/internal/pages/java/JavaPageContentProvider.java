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
package rabbit.ui.internal.pages.java;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.ui.internal.pages.AbstractValueContentProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Content provider accepts input as {@code Collection<JavaDataDescriptor>}
 */
public class JavaPageContentProvider extends AbstractValueContentProvider {

  /*
   * This content provider will only show the following elements inside a java
   * class type:
   * 
   * Java types, and methods. Includes inner types such as inner
   * classes/interfaces, and their methods.
   * 
   * Elements such as fields, imports, anonymous inner classes are hidden away.
   * For a number of reasons, for example: imports are not that useful to see;
   * anonymous classes do not have unique identifiers; the concept of "time
   * spent working on a field" is not practical.
   * 
   * Hiding those elements in the viewer will NOT affect the duration of parent
   * elements or any other elements, because the hidden elements are still in
   * the tree mode, we still use those to calculate duration for parent
   * elements.
   */

  /*
   * The following categories are used to structure the data:
   * 
   * JavaCategory.DATE,
   * JavaCategory.PROJECT, 
   * JavaCategory.PACKAGE_ROOT, 
   * JavaCategory.PACKAGE,
   * JavaCategory.TYPE_ROOT,
   * JavaCategory.MEMBER,
   * 
   * The following categories are used to paint the corresponding elements in
   * the viewer:
   * 
   * JavaCategory.DATE
   * JavaCategory.PROJECT, 
   * JavaCategory.PACKAGE_ROOT, 
   * JavaCategory.PACKAGE,
   * JavaCategory.TYPE_ROOT,
   * JavaCategory.TYPE,
   * JavaCategory.METHOD,
   * 
   * The difference between the two is that when we structure the data, we use
   * JavaCategory.MEMBER instead of JavaCategory.TYPE and JavaCategory.METHOD, 
   * MEMBER includes both TYPE and METHOD so that the structure of the class is
   * maintained when we build the tree. 
   */
  
  /**
   * Constructs a new content provider for the given viewer.
   * @param treeViewer The viewer.
   * @throws NullPointerException If the viewer is null.
   */
  public JavaPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
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
  
  @Override
  public void setPaintCategory(ICategory cat) {
    super.setPaintCategory(cat);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);

    Collection<JavaDataDescriptor> data = null;
    try {
      data = (Collection<JavaDataDescriptor>) newInput;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }
    
    Map<String, IJavaElement> elementCache = Maps.newHashMap();
    for (JavaDataDescriptor des : data) {
      
      IJavaElement element = elementCache.get(des.getHandleIdentifier());
      if (element == null) {
        element = des.findElement();
        if (element == null) {
          continue;
        } else {
          elementCache.put(des.getHandleIdentifier(), element);
        }
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
            
            // We don't want to show any of the fields and anonymous classes:
            if (isAnonymousType(e) || isField(e)) {
              break;
            }
            
            if (categorizer.apply(e)) {
              node = TreeNodes.findOrAppend(node, e);
            }
          }
        }
      }
      TreeNodes.appendToParent(node, des.getDuration().getMillis(), true);
    }
  }
  
  @Override
  protected ICategory[] getAllSupportedCategories() {
    // Not that we exclude TYPE and METHOD, because MEMBER includes both:
    List<JavaCategory> categories = Lists.newArrayList(JavaCategory.values());
    categories.remove(JavaCategory.TYPE);
    categories.remove(JavaCategory.METHOD);
    return categories.toArray(new ICategory[categories.size()]);
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
        JavaCategory.MEMBER,
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
        .put(JavaCategory.METHOD,    or(instanceOf(IMethod.class), 
                                        instanceOf(IInitializer.class)))
        .put(JavaCategory.MEMBER,       instanceOf(IMember.class))
        .build();
  }

  @Override
  protected boolean isPaintCategory(ICategory category) {
    return (category instanceof JavaCategory);
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

  /**
   * Checks whether the given Java element is an anonymous type.
   * @param type The element to check.
   * @return True if the element is anonymous, false otherwise.
   */
  private boolean isAnonymousType(IJavaElement type) {
    if (type.getElementType() == IJavaElement.TYPE) {
      return type.getParent().getElementType() == IJavaElement.METHOD;
    }
    return false;
  }
  
  /**
   * Checks whether the given Java element is a field.
   * @param element The element to check.
   * @return True if the element is a field, false otherwise.
   */
  private boolean isField(IJavaElement element) {
    return element.getElementType() == IJavaElement.FIELD;
  }
}
