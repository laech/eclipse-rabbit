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
package rabbit.data.handler;

import rabbit.data.IFileStore;
import rabbit.data.access.IAccessor;
import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;
import rabbit.data.xml.FileStore;
import rabbit.data.xml.access.CommandDataAccessor;
import rabbit.data.xml.access.FileDataAccessor;
import rabbit.data.xml.access.LaunchDataAccessor;
import rabbit.data.xml.access.PartDataAccessor;
import rabbit.data.xml.access.PerspectiveDataAccessor;
import rabbit.data.xml.access.SessionDataAccessor;
import rabbit.data.xml.access.TaskFileDataAccessor;
import rabbit.data.xml.store.CommandEventStorer;
import rabbit.data.xml.store.FileEventStorer;
import rabbit.data.xml.store.LaunchEventStorer;
import rabbit.data.xml.store.PartEventStorer;
import rabbit.data.xml.store.PerspectiveEventStorer;
import rabbit.data.xml.store.SessionEventStorer;
import rabbit.data.xml.store.TaskFileEventStorer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.ImmutableDescriptor;

/**
 * Handler class provider common classes to access the data.
 * TODO test
 */
public class DataHandler {

  /** Map<T, IStorer<T> */
  private static final BiMap<Class<?>, IStorer<?>> storers;

  private static final IAccessor<LaunchDataDescriptor> launchDataAccessor;
  private static final IAccessor<PerspectiveDataDescriptor> perspectiveDataAccessor;
  private static final IAccessor<CommandDataDescriptor> commandDataAccessor;
  private static final IAccessor<SessionDataDescriptor> sessionDataAccessor;
  private static final IAccessor<PartDataDescriptor> partDataAccessor;
  private static final IAccessor<FileDataDescriptor> fileDataAccessor;
  private static final IAccessor<TaskFileDataDescriptor> taskFileDataAccessor;
  
  private static final BiMap<Class<?>, IAccessor<?>> accessors;

  static {
    storers = ImmutableBiMap.<Class<?>, IStorer<?>>builder()
        .put(PerspectiveEvent.class, PerspectiveEventStorer.getInstance())
        .put(CommandEvent.class, CommandEventStorer.getInstance())
        .put(FileEvent.class, FileEventStorer.getInstance())
        .put(PartEvent.class, PartEventStorer.getInstance())
        .put(LaunchEvent.class, LaunchEventStorer.getInstance())
        .put(TaskFileEvent.class, TaskFileEventStorer.getInstance())
    // TODO test
        .put(SessionEvent.class, SessionEventStorer.getInstance())
        .build();

    perspectiveDataAccessor = new PerspectiveDataAccessor();
    commandDataAccessor = new CommandDataAccessor();
    sessionDataAccessor = new SessionDataAccessor();
    launchDataAccessor = new LaunchDataAccessor();
    partDataAccessor = new PartDataAccessor();
    fileDataAccessor = new FileDataAccessor();
    taskFileDataAccessor = new TaskFileDataAccessor();
    
    accessors = ImmutableBiMap.<Class<?>, IAccessor<?>>builder()
        .put(PerspectiveDataDescriptor.class, new PerspectiveDataAccessor())
        .put(CommandDataDescriptor.class, new CommandDataAccessor())
        .put(SessionDataDescriptor.class, new SessionDataAccessor())
        .put(LaunchDataDescriptor.class, new LaunchDataAccessor())
        .put(PartDataDescriptor.class, new PartDataAccessor())
        .put(FileDataDescriptor.class, new FileDataAccessor())
        .put(TaskFileDataDescriptor.class, new TaskFileDataAccessor())
        .build();
  }

  /**
   * Gets an IAccessor for accessing the command event data.
   * 
   * @return An IAccessor for accessing the command event data.
   */
  public static IAccessor<CommandDataDescriptor> getCommandDataAccessor() {
    return commandDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the file event data.
   * 
   * @return An IAccessor for accessing the file event data.
   */
  public static IAccessor<FileDataDescriptor> getFileDataAccessor() {
    return fileDataAccessor;
  }

  /**
   * Gets the resource manager.
   * 
   * @return The resource manager.
   */
  public static IFileStore getFileStore() {
    return FileStore.INSTANCE;
  }

  /**
   * Gets an IAccessor for accessing the launch event data.
   * 
   * @return An IAccessor for accessing the launch event data.
   */
  public static IAccessor<LaunchDataDescriptor> getLaunchDataAccessor() {
    return launchDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the part event data.
   * 
   * @return An IAccessor for accessing the part event data.
   */
  public static IAccessor<PartDataDescriptor> getPartDataAccessor() {
    return partDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the perspective event data.
   * 
   * @return An IAccessor for accessing the perspective event data.
   */
  public static IAccessor<PerspectiveDataDescriptor> getPerspectiveDataAccessor() {
    return perspectiveDataAccessor;
  }

  /**
   * Gets an IAccessor for accessing the session event data.
   * 
   * @return An IAccessor for accessing the session event data.
   */
  public static IAccessor<SessionDataDescriptor> getSessionDataAccessor() {
    return sessionDataAccessor;
  }

  /**
   * Gets a storer that stores the objects of the given type.
   * <p>
   * The following object types are supported:
   * <ul>
   * <li>{@link CommandEvent}</li>
   * <li>{@link FileEvent}</li>
   * <li>{@link PartEvent}</li>
   * <li>{@link PerspectiveEvent}</li>
   * <li>{@link LaunchEvent}</li>
   * <li>{@link TaskFileEvent}</li>
   * <li>{@link SessionEvent}</li>
   * </ul>
   * </p>
   * 
   * @param <T> The type of the objects that the storer can store.
   * @param objectClass The class of the type.
   * @return A storer that stores the objects of the given type, or null.
   * @throws NullPointerException If null is passed in.
   */
  @SuppressWarnings("unchecked")
  public static <T> IStorer<T> getStorer(Class<T> objectClass) {
    if (null == objectClass) {
      throw new NullPointerException();
    }
    Object storer = storers.get(objectClass);
    return (null == storer) ? null : (IStorer<T>) storer;
  }

  /**
   * Gets an IAccessor to get the task file event data.
   * 
   * @return An IAccessor to get the data stored.
   */
  public static IAccessor<TaskFileDataDescriptor> getTaskFileDataAccessor() {
    return taskFileDataAccessor;
  }

  private DataHandler() {
  }
}
