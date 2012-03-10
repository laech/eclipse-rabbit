/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.workbench.internal.tracking;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import rabbit.tracking.IPersistableEventListener;
import rabbit.workbench.internal.tracking.ICommandEventListener;
import rabbit.workbench.internal.tracking.IFileEventListener;
import rabbit.workbench.internal.tracking.ILaunchEventListener;
import rabbit.workbench.internal.tracking.IPartEventListener;
import rabbit.workbench.internal.tracking.IPerspectiveEventListener;
import rabbit.workbench.internal.tracking.ISessionEventListener;
import rabbit.workbench.internal.tracking.WorkbenchPlugin;

@RunWith(Parameterized.class)
public final class WorkbenchPluginListenerTest {

  private enum ListenerType {
    COMMAND("commandEventListener", ICommandEventListener.class) {
      @Override Set<ICommandEventListener> getListener(WorkbenchPlugin plugin) {
        return plugin.getCommandEventListeners();
      }
    },
    FILE("fileEventListener", IFileEventListener.class) {
      @Override Set<IFileEventListener> getListener(WorkbenchPlugin plugin) {
        return plugin.getFileEventListeners();
      }
    },
    LAUNCH("launchEventListener", ILaunchEventListener.class) {
      @Override Set<ILaunchEventListener> getListener(WorkbenchPlugin plugin) {
        return plugin.getLaunchEventListeners();
      }
    },
    PART("partEventListener", IPartEventListener.class) {
      @Override Set<IPartEventListener> getListener(WorkbenchPlugin plugin) {
        return plugin.getPartEventListeners();
      }
    },
    PERSPECTIVE("perspectiveEventListener", IPerspectiveEventListener.class) {
      @Override Set<IPerspectiveEventListener> getListener(
          WorkbenchPlugin plugin) {
        return plugin.getPerspectiveEventListeners();
      }
    },
    SESSION("sessionEventListener", ISessionEventListener.class) {
      @Override Set<ISessionEventListener> getListener(WorkbenchPlugin plugin) {
        return plugin.getSessionEventListeners();
      }
    };

    private final Class<?> listenerClass;
    private final String elementName;

    ListenerType(String elementName, Class<?> listenerClass) {
      this.elementName = elementName;
      this.listenerClass = listenerClass;
    }

    public String getElementName() {
      return elementName;
    }

    public Class<?> getListenerClass() {
      return listenerClass;
    }

    abstract Set<? extends IPersistableEventListener<?>> getListener(
        WorkbenchPlugin plugin);
  }

  @Parameters public static Collection<Object[]> data() {
    ListenerType[] values = ListenerType.values();
    List<Object[]> data = newArrayListWithCapacity(values.length);
    for (ListenerType value : values) {
      data.add(new Object[]{value});
    }
    return data;
  }

  private final WorkbenchPlugin plugin;
  private final ListenerType listenerType;

  public WorkbenchPluginListenerTest(ListenerType listenerType) {
    this.listenerType = listenerType;
    this.plugin = WorkbenchPlugin.getDefault();
  }

  @Test public void loadsEventListeners() throws Exception {
    Object listener = mock(listenerType.getListenerClass());
    IConfigurationElement element = mock(IConfigurationElement.class);
    given(element.getName()).willReturn(listenerType.getElementName());
    given(element.createExecutableExtension("class")).willReturn(listener);

    IExtensionRegistry registry = mock(IExtensionRegistry.class);
    IConfigurationElement[] elements = {element};
    given(registry.getConfigurationElementsFor(
        "rabbit.tracking.workbench.listeners")).willReturn(elements);
    given(registry.getConfigurationElementsFor(
        "rabbit.tracking.workbench", "listeners")).willReturn(elements);

    plugin.loadListeners(registry);
    assertThat(message(), listenerType.getListener(plugin).size(), is(1));
    assertThat(message(), listenerType.getListener(plugin).contains(listener),
        is(true));
  }

  @Test(expected = UnsupportedOperationException.class)//
  public void returnsEventListenersAsUnmodifiable() {
    listenerType.getListener(plugin).clear();
  }

  private String message() {
    return listenerType.name();
  }
}
