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
package rabbit.data.test.store.model;

import rabbit.data.store.model.FileEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @see FileEvent
 */
public class FileEventTest extends ContinuousEventTest {

  @Test(expected = IllegalArgumentException.class)
  public void testContructor_fileIdEmpty() {
    createEvent(new DateTime(), 10, "");
  }

  @Test(expected = NullPointerException.class)
  public void testContructor_fileIdNull() {
    createEvent(new DateTime(), 10, null);
  }

  @Test
  public void testContructor_fileIdWhitespace() {
    try {
      createEvent(new DateTime(), 10, " ");
      fail();
    } catch (IllegalArgumentException e) {
    }
    
    try {
      createEvent(new DateTime(), 10, "\t");
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      createEvent(new DateTime(), 10, "\n");
      fail();
    } catch (IllegalArgumentException e) {
    }
    
    try {
      createEvent(new DateTime(), 10, "\r");
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      createEvent(new DateTime(), 10, "\f");
      fail();
    } catch (IllegalArgumentException e) {
    }
    

    try {
      createEvent(new DateTime(), 10, "\tsdflkj");
      fail();
    } catch (IllegalArgumentException e) {
    }
    

    try {
      createEvent(new DateTime(), 10, "lkjasdf ");
      fail();
    } catch (IllegalArgumentException e) {
    }
    

    try {
      createEvent(new DateTime(), 10, "asdkfj aoiujf");
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testGetFileId() {
    String fileId = System.currentTimeMillis() + "";
    FileEvent event = createEvent(new DateTime(), 10, fileId);
    assertEquals(fileId, event.getFileId());
  }

  @Override
  protected final FileEvent createEvent(DateTime time, long duration) {
    return createEvent(time, duration, System.currentTimeMillis() + "");
  }
  
  /**
   * @see FileEvent#FileEvent(DateTime, long, String)
   */
  protected FileEvent createEvent(DateTime time, long duration, String fileId) {
    return new FileEvent(time, duration, fileId);
  }
}
