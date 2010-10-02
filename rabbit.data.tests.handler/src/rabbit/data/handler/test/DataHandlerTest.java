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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * @see DataHandler
 */
public class DataHandlerTest {

  @Test
  public void testGetStorer_notNull() {
    assertNotNull(DataHandler.getStorer(PerspectiveEvent.class));
    assertNotNull(DataHandler.getStorer(CommandEvent.class));
    assertNotNull(DataHandler.getStorer(FileEvent.class));
    assertNotNull(DataHandler.getStorer(PartEvent.class));
    assertNotNull(DataHandler.getStorer(SessionEvent.class));
    assertNotNull(DataHandler.getStorer(TaskFileEvent.class));
    assertNotNull(DataHandler.getStorer(LaunchEvent.class));
    assertNotNull(DataHandler.getStorer(JavaEvent.class));
  }
  
  @Test
  public void testGetStorer_null() {
    assertNull(DataHandler.getStorer(Object.class));
  }

  @Test
  public void testGetAccessor_notNull() {
    assertNotNull(DataHandler.getAccessor(PerspectiveDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(CommandDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(FileDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(PartDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(SessionDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(TaskFileDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(LaunchDataDescriptor.class));
    assertNotNull(DataHandler.getAccessor(JavaDataDescriptor.class));
  }

  @Test
  public void testGetAccessor_null() {
    assertNull(DataHandler.getAccessor(Object.class));
  }
}
