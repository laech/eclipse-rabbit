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
package rabbit.data.internal.xml.access;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.access.model.TaskFileDataDescriptor;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * TODO
 */
public class AccessorModule extends AbstractModule {

  public AccessorModule() {
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<IAccessor<CommandDataDescriptor>>() {}).to(
        CommandDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<FileDataDescriptor>>() {}).to(
        FileDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<JavaDataDescriptor>>() {}).to(
        JavaDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<LaunchDataDescriptor>>() {}).to(
        LaunchDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<PartDataDescriptor>>() {}).to(
        PartDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<PerspectiveDataDescriptor>>() {}).to(
        PerspectiveDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<SessionDataDescriptor>>() {}).to(
        SessionDataAccessor.class).in(Singleton.class);

    bind(new TypeLiteral<IAccessor<TaskFileDataDescriptor>>() {}).to(
        TaskFileDataAccessor.class).in(Singleton.class);

  }
}
