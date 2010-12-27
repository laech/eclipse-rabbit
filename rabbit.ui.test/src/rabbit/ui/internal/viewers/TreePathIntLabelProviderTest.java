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
package rabbit.ui.internal.viewers;

import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Tests for {@link TreePathIntLabelProvider}.
 */
public class TreePathIntLabelProviderTest {

  private static Display display;
  private TreeViewer viewer;
  private TreeViewerColumn column;

  @BeforeClass
  public static void beforeClass() {
    display = new Display();
  }

  @AfterClass
  public static void afterClass() {
    display.dispose();
  }

  @Before
  public void before() {
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    viewer = new TreeViewer(shell);
    column = new TreeViewerColumn(viewer, SWT.LEFT);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithoutAValueProvider() {
    new TreePathIntLabelProvider(null);
  }

  @Test
  public void getValueProviderShouldReturnTheValueProvider() {
    IValueProvider valueProvider = mock(IValueProvider.class);
    TreePathIntLabelProvider labelProvider = new TreePathIntLabelProvider(valueProvider);
    assertThat(labelProvider.getValueProvider(), is(valueProvider));
  }

  @Test
  public void updateShouldSetTheCellTextToTheValueOfThePathIfThePathIsToBePainted() {
    Integer value = Integer.valueOf(18);
    Object obj = new Object();

    IValueProvider valueProvider = mock(IValueProvider.class);
    TreePath equalPath = new TreePath(new Object[]{obj});
    given(valueProvider.getValue(equalPath)).willReturn(value.longValue());
    given(valueProvider.shouldPaint(obj)).willReturn(TRUE);

    TreePathIntLabelProvider labelProvider = new TreePathIntLabelProvider(valueProvider);
    column.setLabelProvider(labelProvider);

    ITreeContentProvider contentProvider = mock(ITreeContentProvider.class);
    given(contentProvider.getElements("")).willReturn(new Object[]{obj});
    viewer.setContentProvider(contentProvider);
    viewer.setInput("");

    String expectedText = String.valueOf(value);
    assertThat(viewer.getTree().getItem(0).getText(), is(expectedText));
  }

  @Test
  public void updateShouldSetTheCellTextToBlankIfThePathShouldNotBePainted() {
    Object obj = new Object();

    IValueProvider valueProvider = mock(IValueProvider.class);
    TreePath equalPath = new TreePath(new Object[]{obj});
    given(valueProvider.shouldPaint(equalPath)).willReturn(FALSE);

    TreePathIntLabelProvider labelProvider = new TreePathIntLabelProvider(valueProvider);
    column.setLabelProvider(labelProvider);

    ITreeContentProvider contentProvider = mock(ITreeContentProvider.class);
    given(contentProvider.getElements("")).willReturn(new Object[]{obj});
    viewer.setContentProvider(contentProvider);
    viewer.setInput("");

    assertThat(viewer.getTree().getItem(0).getText(), is(""));
  }
}
