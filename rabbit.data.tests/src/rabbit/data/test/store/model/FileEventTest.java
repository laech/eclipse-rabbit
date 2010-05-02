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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @see FileEvent
 */
public class FileEventTest extends ContinuousEventTest {

  @Test(expected = NullPointerException.class)
  public void testContructor_fileIdNull() {
    createEvent(new DateTime(), 10, null);
  }

  @Test
  public void testGetFilePath() {
    IPath path = Path.fromPortableString("/project/folder/me.txt");
    FileEvent event = createEvent(new DateTime(), 10, path);
    assertEquals(path, event.getFilePath());
  }

  @Override
  protected final FileEvent createEvent(DateTime time, long duration) {
    return createEvent(time, duration, Path.fromPortableString("/p/f/a.txt"));
  }
  
  /**
   * @see FileEvent#FileEvent(DateTime, long, IPath)
   */
  protected FileEvent createEvent(DateTime time, long duration, IPath filePath) {
    return new FileEvent(time, duration, filePath);
  }
}
