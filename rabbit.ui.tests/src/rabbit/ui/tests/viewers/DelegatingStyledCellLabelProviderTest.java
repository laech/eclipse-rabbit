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

import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see DelegatingStyledCellLabelProvider
 */
@SuppressWarnings("restriction")
public class DelegatingStyledCellLabelProviderTest {
  
  private static FullLabelProvider labels;
  
  private static Shell shell;
  private static TableViewer viewer;
  
  @BeforeClass
  public static void beforeClass() {
    labels = new FullLabelProvider();
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    viewer = new TableViewer(shell, SWT.NONE);
    viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(
        labels, true));
    viewer.setContentProvider(new IStructuredContentProvider() {
      @Override public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
      @Override public void dispose() {}
      @Override
      public Object[] getElements(Object inputElement) {
        return new Object[] { inputElement };
      }
    });
    shell.open();
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }
  
  @Before
  public void before() {
    viewer.getTable().removeAll();
  }
  
  @Test
  public void testBackground() throws Exception {
    Object element = new Object();
    viewer.setInput(element);
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(labels.getBackground(element), item.getBackground(0));
  }
  
  @Test
  public void testForeground() throws Exception {
    Object element = new Object();
    viewer.setInput(element);
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(labels.getForeground(element), item.getForeground(0));
  }
  
  @Test
  public void testFont() throws Exception {
    Object element = new Object();
    viewer.setInput(element);
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(labels.getFont(element), item.getFont(0));
  }
  
  @Test
  public void testText() throws Exception {
    Object element = new Object();
    viewer.setInput(element);
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(labels.getText(element), item.getText(0));
  }
  
  @Test
  public void testImage() throws Exception {
    Object element = new Object();
    viewer.setInput(element);
    TableItem item = viewer.getTable().getItem(0);
    assertEquals(labels.getImage(element), item.getImage(0));
  }
  
  @Test
  public void testDispose() throws Exception {
    final Color color = new Color(shell.getDisplay(), 1, 1, 1);
    FullLabelProvider labelProvider = new FullLabelProvider() {
      @Override public Color getBackground(Object element) {
        return color;
      }
      @Override public void dispose() {
        super.dispose();
        color.dispose();
      }
    };
    
    new DelegatingStyledCellLabelProvider(labelProvider, false).dispose();
    assertFalse(color.isDisposed());
    
    new DelegatingStyledCellLabelProvider(labelProvider, true).dispose();
    if (!color.isDisposed()) {
      color.dispose();
      fail("Label provider is not disposed");
    }
  }
  
  /**
   * A label provider to help testing. All methods returns non null values.
   */
  private static class FullLabelProvider extends LabelProvider 
      implements IFontProvider, IColorProvider {
    
    @Override
    public String getText(Object element) {
      return "HelloWorldAbc123";
    }
    
    @Override
    public Image getImage(Object element) {
      return PlatformUI.getWorkbench().getSharedImages().getImage(
          ISharedImages.IMG_DEC_FIELD_ERROR);
    }
    
    @Override
    public Font getFont(Object element) {
      return JFaceResources.getBannerFont();
    }

    @Override
    public Color getBackground(Object element) {
      return PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
    }

    @Override
    public Color getForeground(Object element) {
      return PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
    }
  }
}
