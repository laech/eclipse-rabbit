/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.tests.viewers;

import rabbit.ui.internal.viewers.DeepPatternFilter;

import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see DeepPatternFilter
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class DeepPatternFilterTest {
  
  private static IContentProvider contentProvider = new ITreeContentProvider() {
    
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
    @Override
    public void dispose() {
    }
    
    @Override
    public Object[] getElements(Object inputElement) {
      return (Object[])  inputElement;
    }
    
    @Override
    public boolean hasChildren(Object element) {
      return ((String) element).length() > 0;
    }
    
    @Override
    public Object getParent(Object element) {
      return null;
    }
    
    @Override
    public Object[] getChildren(Object parentElement) {
      String s = (String) parentElement;
      if (s.length() == 0) {
        return new Object[0];
      } else {
        return new Object[] { s.substring(0, s.length() - 1) }; 
      }
    }
  };

  private static FilteredTree tree;
  private static DeepPatternFilter filter;
  private static Shell shell;

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    filter = new DeepPatternFilter();
    tree = new FilteredTree(shell, 0, filter);
    tree.getViewer().setContentProvider(contentProvider);
  }
  
  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }
  
  @Test
  public void testIsElementVisible() {
    tree.getViewer().setInput(new String[] { "HelloWorld", "Blah"});
    filter.setPattern("he");
    assertTrue(filter.isElementVisible(tree.getViewer(), "HelloWorld"));
    assertTrue(filter.isElementVisible(tree.getViewer(), "HelloWorl"));
    assertTrue(filter.isElementVisible(tree.getViewer(), "HelloWor"));
    assertTrue(filter.isElementVisible(tree.getViewer(), "HelloWo"));
    assertTrue(filter.isElementVisible(tree.getViewer(), "HelloW"));
    assertTrue(filter.isElementVisible(tree.getViewer(), "Hello"));
  }
}
