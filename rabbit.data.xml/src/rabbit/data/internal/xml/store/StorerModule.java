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
package rabbit.data.internal.xml.store;

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.CommandEvent;
import rabbit.data.store.model.FileEvent;
import rabbit.data.store.model.JavaEvent;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.store.model.PartEvent;
import rabbit.data.store.model.PerspectiveEvent;
import rabbit.data.store.model.SessionEvent;
import rabbit.data.store.model.TaskFileEvent;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * TODO
 */
public class StorerModule extends AbstractModule {

  /**
   * Constructor.
   */
  public StorerModule() {

  }

  @Override
  protected void configure() {
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.COMMAND_STORE))
        .toInstance(DataStore.COMMAND_STORE);
    bind(new TypeLiteral<IStorer<CommandEvent>>() {})
        .to(CommandEventStorer.class)
        .in(Singleton.class);
    
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.FILE_STORE))
        .toInstance(DataStore.FILE_STORE);
    bind(new TypeLiteral<IStorer<FileEvent>>() {})
        .to(FileEventStorer.class)
        .in(Singleton.class);
    
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.JAVA_STORE))
        .toInstance(DataStore.JAVA_STORE);
    bind(new TypeLiteral<IStorer<JavaEvent>>(){})
        .to(JavaEventStorer.class)
        .in(Singleton.class);

    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.LAUNCH_STORE))
        .toInstance(DataStore.LAUNCH_STORE);
    bind(new TypeLiteral<IStorer<LaunchEvent>>(){})
        .to(LaunchEventStorer.class)
        .in(Singleton.class);

    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.PART_STORE))
        .toInstance(DataStore.PART_STORE);
    bind(new TypeLiteral<IStorer<PartEvent>>(){})
        .to(PartEventStorer.class)
        .in(Singleton.class);

    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.PERSPECTIVE_STORE))
        .toInstance(DataStore.PERSPECTIVE_STORE);
    bind(new TypeLiteral<IStorer<PerspectiveEvent>>() {})
        .to(PerspectiveEventStorer.class)
        .in(Singleton.class);

    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.SESSION_STORE))
        .toInstance(DataStore.SESSION_STORE);
    bind(new TypeLiteral<IStorer<SessionEvent>>() {})
        .to(SessionEventStorer.class)
        .in(Singleton.class);

    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.TASK_STORE))
        .toInstance(DataStore.TASK_STORE);
    bind(new TypeLiteral<IStorer<TaskFileEvent>>() {})
        .to(TaskFileEventStorer.class)
        .in(Singleton.class);
  }

}
