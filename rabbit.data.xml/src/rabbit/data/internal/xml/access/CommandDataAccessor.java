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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.CommandEventTypeMerger;
import rabbit.data.internal.xml.schema.events.CommandEventListType;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.EventListType;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses command event data.
 */
public class CommandDataAccessor
    extends
    AbstractDataNodeAccessor<CommandDataDescriptor, CommandEventType, CommandEventListType> {

  public CommandDataAccessor() {
  }

  @Override
  protected CommandDataDescriptor createDataNode(LocalDate cal,
      CommandEventType type) {

    try {
      return new CommandDataDescriptor(cal, type.getCount(), type
          .getCommandId());

    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<CommandEventListType> getCategories(EventListType doc) {
    return doc.getCommandEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.COMMAND_STORE;
  }

  @Override
  protected Collection<CommandEventType> getElements(CommandEventListType list) {
    return list.getCommandEvent();
  }

  @Override
  protected CommandEventTypeMerger createMerger() {
    return new CommandEventTypeMerger();
  }

}
