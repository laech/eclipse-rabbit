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

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;
import rabbit.data.xml.access.CommandDataAccessor;
import rabbit.data.xml.access.FileDataAccessor;
import rabbit.data.xml.access.JavaDataAccessor;
import rabbit.data.xml.access.LaunchDataAccessor;
import rabbit.data.xml.access.PartDataAccessor;
import rabbit.data.xml.access.PerspectiveDataAccessor;
import rabbit.data.xml.access.SessionDataAccessor;
import rabbit.data.xml.access.TaskFileDataAccessor;
import rabbit.data.xml.store.CommandEventStorer;
import rabbit.data.xml.store.FileEventStorer;
import rabbit.data.xml.store.JavaEventStorer;
import rabbit.data.xml.store.LaunchEventStorer;
import rabbit.data.xml.store.PartEventStorer;
import rabbit.data.xml.store.PerspectiveEventStorer;
import rabbit.data.xml.store.SessionEventStorer;
import rabbit.data.xml.store.TaskFileEventStorer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Handler class provider common classes to access the data.
 */
public class DataHandler {

  /** Unmodifiable BiMap<T, IStorer<T> */
  private static final BiMap<Class<?>, IStorer<?>> storers;
  
  /** Unmodifiable BiMap<T, IAccessor<T> */
  private static final BiMap<Class<?>, IAccessor<?>> accessors;

  static {
    storers = ImmutableBiMap.<Class<?>, IStorer<?>>builder()
        .put(PerspectiveEvent.class, PerspectiveEventStorer.getInstance())
        .put(CommandEvent.class, CommandEventStorer.getInstance())
        .put(FileEvent.class, FileEventStorer.getInstance())
        .put(PartEvent.class, PartEventStorer.getInstance())
        .put(LaunchEvent.class, LaunchEventStorer.getInstance())
        .put(TaskFileEvent.class, TaskFileEventStorer.getInstance())
        .put(SessionEvent.class, SessionEventStorer.getInstance())
        .put(JavaEvent.class, JavaEventStorer.getInstance())
        .build();

    accessors = ImmutableBiMap.<Class<?>, IAccessor<?>>builder()
        .put(PerspectiveDataDescriptor.class, new PerspectiveDataAccessor())
        .put(CommandDataDescriptor.class, new CommandDataAccessor())
        .put(SessionDataDescriptor.class, new SessionDataAccessor())
        .put(LaunchDataDescriptor.class, new LaunchDataAccessor())
        .put(PartDataDescriptor.class, new PartDataAccessor())
        .put(FileDataDescriptor.class, new FileDataAccessor())
        .put(TaskFileDataDescriptor.class, new TaskFileDataAccessor())
        .put(JavaDataDescriptor.class, new JavaDataAccessor())
        .build();
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
   * <li>TODO {@link JavaEvent}</li>
   * </ul>
   * </p>
   * 
   * @param objectClass The class of the type.
   * @return A storer that stores the objects of the given type, or null.
   */
  @SuppressWarnings("unchecked")
  public static <T> IStorer<T> getStorer(Class<T> objectClass) {
    Object storer = storers.get(objectClass);
    return (null == storer) ? null : (IStorer<T>) storer;
  }
  
  /**
   * Gets an accessor that gets the stored data.
   * <p>
   * The following object types are supported:
   * <ul>
   * <li>{@link CommandDataDescriptor}</li>
   * <li>{@link FileDataDescriptor}</li>
   * <li>{@link PartDataDescriptor}</li>
   * <li>{@link PerspectiveDataDescriptor}</li>
   * <li>{@link LaunchDataDescriptor}</li>
   * <li>{@link TaskFileDataDescriptor}</li>
   * <li>{@link SessionDataDescriptor}</li>
   * <li>TODO {@link JavaDataDescriptor}</li>
   * </ul>
   * </p>
   * 
   * @param objectClass The class of the type.
   * @return An accessor that get the data of the given type, or null.
   */
  @SuppressWarnings("unchecked")
  public static <T> IAccessor<T> getAccessor(Class<T> objectClass) {
    IAccessor<?> accessor = accessors.get(objectClass);
    return (null == accessor) ? null : (IAccessor<T>) accessor;
  }

  private DataHandler() {
  }
}
