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
package rabbit.ui.internal.treebuilders;

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.CommandDataTreeBuilder.ICommandDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider2;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.collect.Lists.newArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.LocalDate;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

/**
 * @see CommandDataTreeBuilder
 */
public class CommandDataTreeBuilderTest
    extends AbstractDataTreeBuilderTest<ICommandData> {

  @Override
  protected List<TreePath> buildPaths(ICommandData d, ICategory... categories) {
    List<Object> segments = newArrayList();
    for (ICategory c : categories) {
      if (c == Category.COMMAND) {
        segments.add(d.get(ICommandData.COMMAND));
      } else if (c == Category.DATE) {
        segments.add(d.get(ICommandData.DATE));
      } else if (c == Category.WORKSPACE) {
        segments.add(d.get(ICommandData.WORKSPACE));
      }
    }
    segments.add(d.get(ICommandData.COUNT));
    return asList(new TreePath(segments.toArray()));
  }

  @Override
  protected ICategory[] categories() {
    return new ICategory[]{Category.DATE, Category.WORKSPACE};
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider2 p) {
    return new CommandDataTreeBuilder(p);
  }

  @Override
  protected ICommandData dataNode1() {
    ICommandData data = mock(ICommandData.class);
    given(data.get(ICommandData.COMMAND)).willReturn(newCommand("1"));
    given(data.get(ICommandData.COUNT)).willReturn(10);
    given(data.get(ICommandData.DATE)).willReturn(new LocalDate());
    given(data.get(ICommandData.WORKSPACE)).willReturn(
        new WorkspaceStorage(new Path("/"), null));
    return data;
  }

  @Override
  protected ICommandData dataNode2() {
    ICommandData data = mock(ICommandData.class);
    given(data.get(ICommandData.COMMAND)).willReturn(newCommand("2"));
    given(data.get(ICommandData.COUNT)).willReturn(11);
    given(data.get(ICommandData.DATE)).willReturn(new LocalDate().minusDays(1));
    given(data.get(ICommandData.WORKSPACE)).willReturn(
        new WorkspaceStorage(new Path("/1"), new Path("/2")));
    return data;
  }

  @Override
  protected IProvider<ICommandData> input(
      final Collection<ICommandData> inputData) {
    return new ICommandDataProvider() {
      @Override
      public Collection<ICommandData> get() {
        return inputData;
      }
    };
  }

  private Command newCommand(String id) {
    try {
      Constructor<Command> c = Command.class.getDeclaredConstructor(String.class);
      c.setAccessible(true);
      return c.newInstance(id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
