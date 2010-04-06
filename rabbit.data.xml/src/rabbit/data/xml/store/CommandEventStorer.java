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
package rabbit.data.xml.store;

import rabbit.data.internal.xml.AbstractStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.convert.CommandEventConverter;
import rabbit.data.internal.xml.merge.CommandEventTypeMerger;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.CommandEventListType;
import rabbit.data.internal.xml.schema.events.CommandEventType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.store.model.CommandEvent;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link CommandEvent}
 */
public final class CommandEventStorer extends
    AbstractStorer<CommandEvent, CommandEventType, CommandEventListType> {

  private static final CommandEventStorer INSTANCE = new CommandEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static CommandEventStorer getInstance() {
    return INSTANCE;
  }

  @Nonnull
  private final CommandEventConverter converter;
  @Nonnull
  private final CommandEventTypeMerger merger;

  private CommandEventStorer() {
    converter = new CommandEventConverter();
    merger = new CommandEventTypeMerger();
  }

  @Override
  protected List<CommandEventListType> getCategories(EventListType events) {
    return events.getCommandEvents();
  }

  @Override
  protected CommandEventConverter getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.COMMAND_STORE;
  }

  @Override
  protected List<CommandEventType> getElements(CommandEventListType list) {
    return list.getCommandEvent();
  }

  @Override
  protected IMerger<CommandEventType> getMerger() {
    return merger;
  }

  @Override
  protected CommandEventListType newCategory(XMLGregorianCalendar date) {
    CommandEventListType type = objectFactory.createCommandEventListType();
    type.setDate(date);
    return type;
  }
}
