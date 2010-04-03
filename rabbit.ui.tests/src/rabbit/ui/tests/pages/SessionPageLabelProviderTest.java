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
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.SessionPage;
import rabbit.ui.internal.pages.SessionPageLabelProvider;
import rabbit.ui.internal.util.MillisConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Test for {@link SessionPageLabelProvider}
 */
public class SessionPageLabelProviderTest {

  private static Shell shell;
  private static SessionPage page;
  private static SessionPageLabelProvider provider;

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new SessionPage();
    page.createContents(shell);
    provider = new SessionPageLabelProvider(page);
  }

  @Test
  public void testGetColumnTextImage() throws Exception {
    Format formatter = new SimpleDateFormat("yyyy-MM-dd EEEE");
    String date = formatter.format(Calendar.getInstance().getTime());
    long value = 187598;
    Map<String, Long> data = SessionPageTest.getData(page);
    data.put(date, value);

    assertEquals(date, provider.getColumnText(date, 0));
    assertEquals(MillisConverter.toDefaultString(value), provider
        .getColumnText(date, 1));

    assertNull(provider.getColumnImage(date, 0));
    assertNull(provider.getColumnImage(date, 1));
  }
}
