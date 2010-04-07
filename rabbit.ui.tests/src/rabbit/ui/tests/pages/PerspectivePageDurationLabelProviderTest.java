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
package rabbit.ui.tests.pages;

import rabbit.data.access.model.PerspectiveDataDescriptor;
import static rabbit.ui.MillisConverter.*;
import rabbit.ui.internal.pages.PerspectivePage;
import rabbit.ui.internal.pages.PerspectivePageContentProvider;
import rabbit.ui.internal.pages.PerspectivePageDurationLabelProvider;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see PerspectivePageDurationLabelProvider
 */
@SuppressWarnings("restriction")
public class PerspectivePageDurationLabelProviderTest {

  private static Shell shell;
  private static PerspectivePage page;
  private static PerspectivePageContentProvider contents;
  private static PerspectivePageDurationLabelProvider labels;

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new PerspectivePage();
    page.createContents(shell);

    contents = new PerspectivePageContentProvider(page, true);
    labels = new PerspectivePageDurationLabelProvider(contents);

    page.getViewer().setContentProvider(contents);
    page.getViewer().setLabelProvider(labels);
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Before
  public void before() {
    page.getViewer().getTree().removeAll();
  }

  @Test
  public void testUpdate_labelOfPerspective_displayByDateDisabled() {
    IPerspectiveDescriptor persp = new UndefinedPerspectiveDescriptor("abc");

    // COnstruct 2 data descriptors using the perspective Id:
    PerspectiveDataDescriptor data1 = new PerspectiveDataDescriptor(
        new LocalDate(), 1287234341, persp.getId());
    PerspectiveDataDescriptor data2 = new PerspectiveDataDescriptor(
        new LocalDate(), 9983248765L, persp.getId());

    // Disable it so that there is no tree items created for the date:
    contents.setDisplayByDate(false);
    page.getViewer().setInput(Arrays.asList(data1, data2));

    // There should only be one item (combination of the two data descriptors,
    // because they are of the same perspective ID) in the tree:
    TreeItem item = page.getViewer().getTree().getItem(0);
    assertEquals(toDefaultString(data1.getValue() + data2.getValue()), item
        .getText());
  }

  @Test
  public void testUpdate_labelOfPerspectiveDataDescriptor_displayByDateEnabled() {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(
        new LocalDate(), 1198348734, "a.b.c");

    // Enable for testing:
    contents.setDisplayByDate(true);
    page.getViewer().setInput(Arrays.asList(des));

    page.getViewer().expandAll();
    // The 0th item is the date because displayByDate is enable, so the child of
    // this item is our perspective:
    TreeItem item = page.getViewer().getTree().getItem(0).getItem(0);
    assertEquals(toDefaultString(des.getValue()), item.getText());
  }
}
