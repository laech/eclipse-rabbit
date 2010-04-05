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

import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.store.model.FileEvent;
import rabbit.data.test.xml.AbstractContinuousEventStorerTest;
import rabbit.data.xml.store.FileEventStorer;

import org.joda.time.DateTime;
import org.junit.Assert;

import java.util.List;

public class FileEventStorerTest
    extends
    AbstractContinuousEventStorerTest<FileEvent, FileEventType, FileEventListType> {

  @Override
  public void testHasSameId_typeAndType() throws Exception {
    String id = "asdfsdf23";
    FileEventType type1 = new ObjectFactory().createFileEventType();
    type1.setFileId(id);
    FileEventType type2 = new ObjectFactory().createFileEventType();
    type2.setFileId(id);
    Assert.assertTrue(hasSameId(storer, type1, type2));
  }

  @Override
  protected FileEventStorer create() {
    return FileEventStorer.getInstance();
  }

  @Override
  protected FileEvent createEvent() {
    return new FileEvent(new DateTime(), 10, "someId");
  }

  @Override
  protected FileEvent createEvent(DateTime eventTime) {
    return new FileEvent(eventTime, 10, "someIdabvc");
  }

  @Override
  protected FileEvent createEvent2() {
    return new FileEvent(new DateTime(), 110, "blah");
  }

  @Override
  protected List<FileEventType> getEventTypes(FileEventListType type) {
    return type.getFileEvent();
  }

  @Override
  protected boolean hasSameId(FileEventType xml, FileEvent e) {
    return xml.getFileId().equals(e.getFileId());
  }

  @Override
  protected boolean isEqual(FileEventType type, FileEvent event) {
    boolean isEqual = type.getFileId().equals(event.getFileId());
    if (isEqual) {
      isEqual = (type.getDuration() == event.getDuration());
    }
    return isEqual;
  }

  @Override
  protected FileEvent mergeValue(FileEvent main, FileEvent tmp) {
    return new FileEvent(main.getTime(),
        tmp.getDuration() + main.getDuration(), main.getFileId());
  }

}
