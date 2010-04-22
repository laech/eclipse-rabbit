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
package rabbit.data.handler.test;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.TaskFileEvent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;

/**
 * @see DataHandler
 */
public class DataHandlerTest {
  
  @Test
  public void testGetCommandDataAccessor() {
    assertNotNull(DataHandler.getCommandDataAccessor());
  }

  @Test
  public void testGetFileDataAccessor() {
    assertNotNull(DataHandler.getFileDataAccessor());
  }

  @Test
  public void testGetLaunchDataAccessor() {
    assertNotNull(DataHandler.getLaunchDataAccessor());
  }

  @Test
  public void testGetPartDataAccessor() {
    assertNotNull(DataHandler.getPartDataAccessor());
  }

  @Test
  public void testGetPerspectiveDataAccessor() {
    assertNotNull(DataHandler.getPerspectiveDataAccessor());
  }

  @Test
  public void testGetResourceManager() {
    Assert.assertNotNull(DataHandler.getFileStore());
  }

  @Test
  public void testGetStorer() {
    assertNotNull(DataHandler.getStorer(PerspectiveEvent.class));
    assertNotNull(DataHandler.getStorer(CommandEvent.class));
    assertNotNull(DataHandler.getStorer(FileEvent.class));
    assertNotNull(DataHandler.getStorer(PartEvent.class));
    assertNull(DataHandler.getStorer(String.class));
  }

  @Test(expected = NullPointerException.class)
  public void testGetStorer_withNull() {
    DataHandler.getStorer(null);
  }

  @Test
  public void testGetTaskFileDataAccessor() {
    assertNotNull(DataHandler.getTaskFileDataAccessor());
  }

  @Test
  public void testGetTaskFileEventStorer() {
    assertNotNull(DataHandler.getStorer(TaskFileEvent.class));
  }
}
