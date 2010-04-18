/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.pages;

import rabbit.data.access.model.CommandDataDescriptor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Content provider for a {@link CommandPage}. Acceptable input is {@code
 * Iterable<CommandDataDescriptor>}.
 */
public class CommandPageContentProvider extends
    AbstractDateCategoryContentProvider {

  @Nonnull
  private final ICommandService service;
  /** All commands, defined and undefined. The keys of the map are command IDs */
  @Nonnull
  private final Map<String, Command> commands;
  /** Maps command to total values. */
  @Nonnull
  private ImmutableMap<Command, Long> commandSummaries;
  /** Maps dates to collections of command data. */
  @Nonnull
  private ImmutableMultimap<LocalDate, CommandDataDescriptor> dateToCommands;
  /** Function to categorize the input by date. */
  @Nonnull
  private Function<CommandDataDescriptor, LocalDate> categorzeByDatesFunction;

  /**
   * Constructor.
   * 
   * @param page The parent page.
   * @param displayByDate True to display by date, false otherwise.
   * @throws NullPointerException If page is null.
   */
  public CommandPageContentProvider(@Nonnull CommandPage page,
      boolean displayByDate) {
    super(page, displayByDate);
    reset();

    service = (ICommandService) PlatformUI.getWorkbench().getService(
        ICommandService.class);

    commands = Maps.newLinkedHashMap();
    for (Command cmd : service.getDefinedCommands()) {
      commands.put(cmd.getId(), cmd);
    }

    categorzeByDatesFunction = new Function<CommandDataDescriptor, LocalDate>() {
      @Override
      public LocalDate apply(CommandDataDescriptor from) {
        return from.getDate();
      }
    };
  }

  @Override
  public Object[] getChildren(Object element) {
    if (element instanceof LocalDate) {
      Collection<CommandDataDescriptor> data = dateToCommands
          .get((LocalDate) element);
      return data.toArray(new Object[data.size()]);
    }
    return EMPTY_ARRAY;
  }

  /**
   * Gets the command with the ID in the given descriptor.
   * 
   * @param des The descriptor with the command ID.
   * @return A command, never null. If there is no command with the given ID, an
   *         undefined command is returned.
   */
  public Command getCommand(CommandDataDescriptor des) {
    Command cmd = commands.get(des.getCommandId());
    if (cmd == null) {
      cmd = service.getCommand(des.getCommandId());
      commands.put(cmd.getId(), cmd);
    }
    return cmd;
  }

  @Override
  public Object[] getElements(Object inputElement) {
    if (isDisplayingByDate()) {
      Collection<LocalDate> dates = dateToCommands.keySet();
      return dates.toArray(new Object[dates.size()]);

    } else {
      return commandSummaries.keySet().toArray(
          new Object[commandSummaries.size()]);
    }
  }

  /**
   * Gets the value of the command.
   * 
   * @param cmd The command.
   * @return The value of the command.
   */
  public long getValueOfCommand(Command cmd) {
    Long value = commandSummaries.get(cmd);
    return (value == null) ? 0 : value;
  }

  @Override
  public boolean hasChildren(Object element) {
    return dateToCommands.containsKey(element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    if (newInput == null) {
      reset();
      return;
    }

    Iterable<CommandDataDescriptor> data = (Iterable<CommandDataDescriptor>) newInput;
    dateToCommands = Multimaps.index(data, categorzeByDatesFunction);

    Map<Command, Long> sums = Maps.newLinkedHashMap();
    for (CommandDataDescriptor des : data) {
      Command cmd = getCommand(des);
      Long value = sums.get(cmd);
      sums.put(cmd, (value != null) ? value + des.getValue() : des.getValue());
    }
    commandSummaries = ImmutableMap.copyOf(sums);

    updateMaxValue();
  }

  @Override
  protected void updateMaxValue() {
    long max = 0;
    if (isDisplayingByDate()) {
      for (CommandDataDescriptor des : dateToCommands.values()) {
        if (des.getValue() > max)
          max = des.getValue();
      }
    } else {
      for (long value : commandSummaries.values()) {
        if (value > max)
          max = value;
      }
    }
    page.setMaxValue(max);
  }

  /** Empties the fields. */
  private void reset() {
    commandSummaries = ImmutableMap.of();
    dateToCommands = ImmutableMultimap.of();
  }
}
