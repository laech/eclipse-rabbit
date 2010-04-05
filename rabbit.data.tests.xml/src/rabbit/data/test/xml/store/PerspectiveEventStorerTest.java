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

import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.test.xml.AbstractContinuousEventStorerTest;
import rabbit.data.xml.store.PerspectiveEventStorer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

import java.util.List;

public class PerspectiveEventStorerTest extends
    AbstractContinuousEventStorerTest //
    <PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  public void testHasSameId_typeAndType() {
    PerspectiveEvent e = createEvent();

    PerspectiveEventType x = new ObjectFactory().createPerspectiveEventType();
    x.setPerspectiveId(e.getPerspective().getId());

    assertTrue(hasSameId(x, e));

    x.setPerspectiveId("");
    assertFalse(hasSameId(x, e));
  }

  @Override
  protected PerspectiveEventStorer create() {
    return PerspectiveEventStorer.getInstance();
  }

  @Override
  protected PerspectiveEvent createEvent() {
    IPerspectiveDescriptor p = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[0];
    return new PerspectiveEvent(new DateTime(), 194, p);
  }

  @Override
  protected PerspectiveEvent createEvent(DateTime eventTime) {
    IPerspectiveDescriptor p = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[0];
    return new PerspectiveEvent(eventTime, 1924, p);
  }

  @Override
  protected PerspectiveEvent createEvent2() {
    IPerspectiveDescriptor p = PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()[1];
    return new PerspectiveEvent(new DateTime(), 11094, p);
  }

  @Override
  protected List<PerspectiveEventType> getEventTypes(
      PerspectiveEventListType type) {
    return type.getPerspectiveEvent();
  }

  @Override
  protected boolean hasSameId(PerspectiveEventType xml, PerspectiveEvent e) {
    return xml.getPerspectiveId().equals(e.getPerspective().getId());
  }

  @Override
  protected boolean isEqual(PerspectiveEventType type, PerspectiveEvent event) {
    boolean isEqual = false;
    isEqual = type.getPerspectiveId().equals(event.getPerspective().getId());
    if (isEqual) {
      isEqual = (type.getDuration() == event.getDuration());
    }
    return isEqual;
  }

  @Override
  protected PerspectiveEvent mergeValue(PerspectiveEvent main,
      PerspectiveEvent tmp) {
    return new PerspectiveEvent(main.getTime(), main.getDuration()
        + tmp.getDuration(), main.getPerspective());
  }
}
