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
package rabbit.data.test.xml.store;

import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;
import rabbit.data.test.xml.AbstractStorerTest;
import rabbit.data.xml.store.PartEventStorer;

import com.google.common.base.Objects;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

/**
 * @see PartEventStorer
 */
@SuppressWarnings("restriction")
public class PartEventStorerTest extends
    AbstractStorerTest<PartEvent, PartEventType, PartEventListType> {

  @Override
  protected PartEvent createEvent(DateTime dateTime) throws Exception {
    try {
      IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView("org.eclipse.ui.views.TaskList");
      return new PartEvent(dateTime, 11110, view);

    } catch (PartInitException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected PartEvent createEventDiff(final DateTime eventTime)
      throws Exception {
    try {
      IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView("org.eclipse.ui.navigator.ProjectExplorer");
      return new PartEvent(eventTime, 110, view);

    } catch (PartInitException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected PartEventStorer createStorer() {
    return PartEventStorer.getInstance();
  }

  @Override
  protected boolean equal(PartEventType t1, PartEventType t2) {
    return Objects.equal(t1.getPartId(), t2.getPartId())
        && t1.getDuration() == t2.getDuration();
  }
}
