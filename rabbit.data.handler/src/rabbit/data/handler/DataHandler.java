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
import rabbit.data.internal.xml.access.AccessorModule;
import rabbit.data.internal.xml.convert.ConverterModule;
import rabbit.data.internal.xml.merge.MergerModule;
import rabbit.data.internal.xml.store.StorerModule;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

/**
 * Handler class provider common classes to access the data.
 */
public class DataHandler {

  private static final Injector injector;

  static {
    injector = Guice.createInjector(
        new AccessorModule(),
        new StorerModule(), 
        new ConverterModule(), 
        new MergerModule());
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
   * <li>{@link JavaEvent}</li>
   * </ul>
   * </p>
   * 
   * @param clazz The class of the type.
   * @return A storer that stores the objects of the given type, or null.
   */
  @SuppressWarnings("unchecked")
  public static <T> IStorer<T> getStorer(Class<T> clazz) {
    try {
      Key<?> k = Key.get(Types.newParameterizedType(IStorer.class, clazz));
      return (IStorer<T>) injector.getInstance(k);

    } catch (ConfigurationException e) {
      return null;
    }
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
   * <li>{@link JavaDataDescriptor}</li>
   * </ul>
   * </p>
   * 
   * @param clazz The class of the type.
   * @return An accessor that get the data of the given type, or null.
   */
  @SuppressWarnings("unchecked")
  public static <T> IAccessor<T> getAccessor(Class<T> clazz) {
    try {
      Key<?> k = Key.get(Types.newParameterizedType(IAccessor.class, clazz));
      return (IAccessor<T>) injector.getInstance(k);

    } catch (ConfigurationException e) {
      return null;
    }
  }

  private DataHandler() {
  }
}
