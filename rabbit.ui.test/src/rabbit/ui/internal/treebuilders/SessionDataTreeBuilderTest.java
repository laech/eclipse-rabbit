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

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.categories.SessionCategory;
import rabbit.ui.internal.treebuilders.SessionDataTreeBuilder.ISessionDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider2;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.collect.Lists.newArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

/**
 * Tests for a {@link SessionDataTreeBuilder}.
 */
public class SessionDataTreeBuilderTest
    extends AbstractDataTreeBuilderTest<ISessionData> {

  @Override
  protected List<TreePath> buildPaths(ISessionData data, ICategory... categories) {
    List<Object> segments = newArrayList();
    for (ICategory c : categories) {
      if (c == SessionCategory.DATE) {
        segments.add(data.get(ISessionData.DATE));
      } else if (c == SessionCategory.WORKSPACE) {
        segments.add(data.get(ISessionData.WORKSPACE));
      }
    }
    segments.add(data.get(ISessionData.DURATION));
    return asList(new TreePath(segments.toArray()));
  }

  @Override
  protected ICategory[] categories() {
    return new ICategory[]{SessionCategory.DATE, SessionCategory.WORKSPACE};
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider2 p) {
    return new SessionDataTreeBuilder(p);
  }

  @Override
  protected ISessionData dataNode1() {
    ISessionData d = mock(ISessionData.class);
    given(d.get(ISessionData.DATE)).willReturn(new LocalDate().minusDays(1));
    given(d.get(ISessionData.DURATION)).willReturn(new Duration(10));
    given(d.get(ISessionData.WORKSPACE)).willReturn(
        new WorkspaceStorage(new Path("/"), null));
    return d;
  }

  @Override
  protected ISessionData dataNode2() {
    ISessionData d = mock(ISessionData.class);
    given(d.get(ISessionData.DATE)).willReturn(new LocalDate());
    given(d.get(ISessionData.DURATION)).willReturn(new Duration(9));
    given(d.get(ISessionData.WORKSPACE)).willReturn(
        new WorkspaceStorage(new Path("/a"), new Path("/b")));
    return d;
  }

  @Override
  protected IProvider<ISessionData> input(Collection<ISessionData> inputData) {
    IProvider<ISessionData> p = mock(ISessionDataProvider.class);
    given(p.get()).willReturn(inputData);
    return p;
  }

}
