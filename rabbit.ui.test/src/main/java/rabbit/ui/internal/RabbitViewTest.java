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
package rabbit.ui.internal;

import rabbit.ui.IPage;
import rabbit.ui.Preference;
import rabbit.ui.internal.extension.PageDescriptor;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.junit.Test;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public final class RabbitViewTest {

  @Test
  public void testDispose() throws Exception {
    final RabbitView view = showRabbitView();
    getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);

    final Field toolkit = RabbitView.class.getDeclaredField("toolkit");
    toolkit.setAccessible(true);
    final FormToolkit theKit = (FormToolkit)toolkit.get(view);
    final Field isDisposed = FormToolkit.class.getDeclaredField("isDisposed");
    isDisposed.setAccessible(true);
    assertTrue((Boolean)isDisposed.get(theKit));
  }

  @Test
  public void testIsSameDate() throws Exception {
    final Calendar cal1 = Calendar.getInstance();
    final Calendar cal2 = (Calendar)cal1.clone();
    assertTrue(RabbitView.isSameDate(cal1, cal2));

    cal2.add(Calendar.SECOND, 1);
    assertTrue(RabbitView.isSameDate(cal1, cal2));

    cal2.add(Calendar.DAY_OF_MONTH, 1);
    assertFalse(RabbitView.isSameDate(cal1, cal2));
  }

  @Test
  public void testUpdate_checkDates() throws Exception {
    final RabbitView view = showRabbitView();
    final Preference pref = getPreference(view);

    final Calendar fromDate = new GregorianCalendar(1999, 1, 1);
    pref.getStartDate().setTimeInMillis(fromDate.getTimeInMillis());

    final Calendar toDate = new GregorianCalendar(2010, 1, 1);
    pref.getEndDate().setTimeInMillis(toDate.getTimeInMillis());

    update(view);

    assertEquals(fromDate.get(YEAR), pref.getStartDate().get(YEAR));
    assertEquals(fromDate.get(MONTH), pref.getStartDate().get(MONTH));
    assertEquals(fromDate.get(DAY_OF_MONTH),
        pref.getStartDate().get(DAY_OF_MONTH));

    assertEquals(toDate.get(YEAR), pref.getEndDate().get(YEAR));
    assertEquals(toDate.get(MONTH), pref.getEndDate().get(MONTH));
    assertEquals(toDate.get(DAY_OF_MONTH), pref.getEndDate().get(DAY_OF_MONTH));
  }

  @Test
  public void testUpdate_checkPageStatus() throws Exception {
    final RabbitView view = showRabbitView();

    IPage visiblePage = null;
    for (PageDescriptor des : RabbitUI.getDefault().getPages()) {
      visiblePage = des.getPage();
      display(view, des.getPage());
    }
    // All pages have been displayed before, so they should all be updated:
    final Map<IPage, Boolean> status = getPageStatus(view);
    for (boolean isPageUpdated : status.values()) {
      assertTrue(isPageUpdated);
    }

    update(view);
    // Now only the current visible page is updated:
    for (Map.Entry<IPage, Boolean> entry : status.entrySet()) {
      if (entry.getKey() == visiblePage) {
        assertTrue(entry.getValue());
      } else {
        assertFalse(entry.getValue());
      }
    }

  }

  @Test
  public void testUpdateDate() {
    final Calendar date = Calendar.getInstance();
    final DateTime widget = new DateTime(getShell(), SWT.NONE);
    widget.setYear(1901);
    widget.setMonth(3);
    widget.setDay(9);
    RabbitView.updateDate(date, widget);
    assertEquals(widget.getYear(), date.get(Calendar.YEAR));
    assertEquals(widget.getMonth(), date.get(Calendar.MONTH));
    assertEquals(widget.getDay(), date.get(Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testUpdateDateTime() {
    final Calendar date = Calendar.getInstance();
    date.set(1999, 2, 3);
    final DateTime widget = new DateTime(getShell(), SWT.NONE);
    RabbitView.updateDateTime(widget, date);
    assertEquals(date.get(Calendar.YEAR), widget.getYear());
    assertEquals(date.get(Calendar.MONTH), widget.getMonth());
    assertEquals(date.get(Calendar.DAY_OF_MONTH), widget.getDay());
  }

  private void display(RabbitView view, IPage page) throws Exception {
    view.display(page);
  }

  @SuppressWarnings("unchecked")
  private Map<IPage, Boolean> getPageStatus(RabbitView view) throws Exception {
    final Field pageStatus = RabbitView.class.getDeclaredField("pageStatus");
    pageStatus.setAccessible(true);
    return (Map<IPage, Boolean>)pageStatus.get(view);
  }

  private Preference getPreference(RabbitView view) throws Exception {
    final Field pref = RabbitView.class.getDeclaredField("preferences");
    pref.setAccessible(true);
    return (Preference)pref.get(view);
  }

  private void update(RabbitView view) throws Exception {
    final Method update = RabbitView.class.getDeclaredMethod("updateView");
    update.setAccessible(true);
    update.invoke(view);
  }

  private Shell getShell() {
    return getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  private RabbitView showRabbitView() throws PartInitException {
    return (RabbitView)getWorkbench()
        .getActiveWorkbenchWindow()
        .getActivePage()
        .showView("rabbit.ui.view.rabbitview");
  }
}
