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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.ui.internal.pages.CommandPage;
import rabbit.ui.internal.pages.CommandPageContentProvider;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;

/**
 * Test for {@link CommandPage}
 */
@SuppressWarnings("restriction")
public class CommandPageTest extends AbstractTreeViewerPageTest {

  @Test
  public void testGetValue() throws Exception {
    CommandDataDescriptor des1;
    CommandDataDescriptor des2;
    des1 = new CommandDataDescriptor(new LocalDate(), 101, "123");
    des2 = new CommandDataDescriptor(new LocalDate(), 9823, des1.getCommandId());

    assertEquals(des1.getValue(), page.getValue(des1));
    assertEquals(des2.getValue(), page.getValue(des2));

    page.getViewer().setInput(Arrays.asList(des1, des2));
    CommandPageContentProvider cp;
    cp = (CommandPageContentProvider) page.getViewer().getContentProvider();
    assertEquals(des1.getValue() + des2.getValue(), page.getValue(cp
        .getCommand(des1)));
  }

  @Test
  public void testUpdate() throws Exception {
    CommandPageContentProvider cp;
    cp = (CommandPageContentProvider) page.getViewer().getContentProvider();
    cp.setDisplayByDate(true);

    CommandDataDescriptor des1;
    CommandDataDescriptor des2;
    des1 = new CommandDataDescriptor(new LocalDate(), 101, "123");
    des2 = new CommandDataDescriptor(new LocalDate(), 9823, des1.getCommandId());
    page.getViewer().setInput(Arrays.asList(des1, des2));

    assertEquals(des2.getValue(), page.getMaxValue());

    cp.setDisplayByDate(false);
    assertEquals(des1.getValue() + des2.getValue(), page.getMaxValue());
  }

  @Override
  protected CommandPage createPage() {
    return new CommandPage();
  }

}
