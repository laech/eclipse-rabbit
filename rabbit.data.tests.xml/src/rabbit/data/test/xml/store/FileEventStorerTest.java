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
import rabbit.data.store.model.FileEvent;
import rabbit.data.test.xml.AbstractStorerTest;
import rabbit.data.xml.store.FileEventStorer;

import com.google.common.base.Objects;

import org.joda.time.DateTime;

/**
 * @see FileEventStorer
 */
@SuppressWarnings("restriction")
public class FileEventStorerTest extends
    AbstractStorerTest<FileEvent, FileEventType, FileEventListType> {

  @Override
  protected FileEventStorer createStorer() {
    return FileEventStorer.getInstance();
  }

  @Override
  protected FileEvent createEvent(DateTime dateTime) {
    return new FileEvent(dateTime, 10, "someId");
  }

  @Override
  protected FileEvent createEventDiff(DateTime dateTime) {
    return new FileEvent(dateTime, 10, "someIdabvc");
  }

  @Override
  protected boolean equal(FileEventType t1, FileEventType t2) {
    return Objects.equal(t1.getFileId(), t2.getFileId())
        && t1.getDuration() == t2.getDuration();
  }

}
