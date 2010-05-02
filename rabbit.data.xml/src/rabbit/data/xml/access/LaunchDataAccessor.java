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
package rabbit.data.xml.access;

import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.LaunchEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import com.google.common.collect.Sets;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Set;

/**
 * Accesses launch event data.
 */
public class LaunchDataAccessor
    extends
    AbstractDataNodeAccessor<LaunchDataDescriptor, LaunchEventType, LaunchEventListType> {

  @Override
  protected LaunchDataDescriptor createDataNode(LocalDate cal,
      LaunchEventType type) {

    try {
      LaunchConfigurationDescriptor des = new LaunchConfigurationDescriptor(
          type.getName(), type.getLaunchModeId(), type.getLaunchTypeId());

      Set<IPath> paths = Sets.newHashSetWithExpectedSize(type.getFilePath().size());
      for (String str : type.getFilePath()) {
        paths.add(new Path(str));
      }
      return new LaunchDataDescriptor(cal, des, type.getCount(), type
          .getTotalDuration(), paths);

    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected Collection<LaunchEventListType> getCategories(EventListType doc) {
    return doc.getLaunchEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.LAUNCH_STORE;
  }

  @Override
  protected IMerger<LaunchEventType> createMerger() {
    return new LaunchEventTypeMerger();
  }

}
