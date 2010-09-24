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
package rabbit.data.test.xml.store;

import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.internal.xml.store.LaunchEventStorer;
import rabbit.data.store.model.LaunchEvent;
import rabbit.data.test.xml.AbstractStorerTest;

import com.google.common.base.Objects;

import org.eclipse.core.internal.registry.ConfigurationElement;
import org.eclipse.core.internal.registry.ConfigurationElementHandle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationType;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventStorer
 */
@SuppressWarnings("restriction")
public class LaunchEventStorerTest extends
    AbstractStorerTest<LaunchEvent, LaunchEventType, LaunchEventListType> {

  // Empty class for testing.
  private static class ConfigurationElementForTest extends
      ConfigurationElementHandle {

    public ConfigurationElementForTest() {
      super(null, 0);
    }

    @Override
    public String getAttribute(String propertyName) {
      return null;
    }

    @Override
    protected ConfigurationElement getConfigurationElement() {
      return null;
    }
  }

  // Empty class for testing.
  private static class LaunchConfigurationForTest extends LaunchConfiguration {

    private ILaunchConfigurationType type = new LaunchConfigurationTypeForTest();

    protected LaunchConfigurationForTest() {
      super("Abc", null);
    }

    @Override
    public ILaunchConfigurationType getType() throws CoreException {
      return type;
    }
  }

  // Empty class for testing.
  private static class LaunchConfigurationTypeForTest extends
      LaunchConfigurationType {

    public LaunchConfigurationTypeForTest() {
      super(new ConfigurationElementForTest());
    }

    @Override
    public String getIdentifier() {
      return "MyTypeIdentifier";
    }

    @Override
    public String getName() {
      return "MyType";
    }
  }

  @Override
  protected LaunchEventStorer createStorer() {
    return LaunchEventStorer.getInstance();
  }

  @Override
  protected LaunchEvent createEvent(DateTime dateTime) {
    long duration = 19823;
    ILaunchConfiguration config = new LaunchConfigurationForTest();
    ILaunch launch = new Launch(config, ILaunchManager.DEBUG_MODE, null);
    Set<IPath> fileIds = new HashSet<IPath>();
    fileIds.add(new Path("/ab1c"));
    fileIds.add(new Path("/d1ef"));
    return new LaunchEvent(new Interval(dateTime, dateTime.plus(duration)),
        launch, config, fileIds);
  }

  @Override
  protected LaunchEvent createEventDiff(DateTime dateTime) {
    long duration = 119823;
    ILaunchConfiguration config = new LaunchConfigurationForTest();
    ILaunch launch = new Launch(config, ILaunchManager.PROFILE_MODE, null);
    Set<IPath> fileIds = new HashSet<IPath>();
    fileIds.add(new Path("/1ab1c"));
    return new LaunchEvent(new Interval(dateTime, dateTime.plus(duration)),
        launch, config, fileIds);
  }

  @Override
  protected boolean equal(LaunchEventType t1, LaunchEventType t2) {
    return Objects.equal(t1.getLaunchModeId(), t2.getLaunchModeId())
        && Objects.equal(t1.getLaunchTypeId(), t2.getLaunchTypeId())
        && Objects.equal(t1.getName(), t2.getName())
        && t1.getCount() == t2.getCount()
        && t1.getTotalDuration() == t2.getTotalDuration()
        && t1.getFilePath().size() == t2.getFilePath().size()
        && t1.getFilePath().containsAll(t2.getFilePath())
        && t2.getFilePath().containsAll(t1.getFilePath());
  }
}
