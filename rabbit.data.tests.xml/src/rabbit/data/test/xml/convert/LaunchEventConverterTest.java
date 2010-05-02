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
package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.LaunchEventConverter;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import java.util.HashSet;
import java.util.Set;

/**
 * @see LaunchEventConverter
 */
@SuppressWarnings("restriction")
public class LaunchEventConverterTest extends
    AbstractConverterTest<LaunchEvent, LaunchEventType> {

  @Override
  protected LaunchEventConverter createConverter() {
    return new LaunchEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    DateTime time = new DateTime();
    long duration = 9823;
    ILaunchConfiguration config = new LaunchConfigurationForTest();
    ILaunch launch = new Launch(config, ILaunchManager.DEBUG_MODE, null);
    Set<IPath> filePaths = new HashSet<IPath>();
    filePaths.add(new Path("/abc"));
    filePaths.add(new Path("/def"));

    LaunchEvent event = new LaunchEvent(time, duration, launch, config, filePaths);
    LaunchEventType type = converter.convert(event);

    assertEquals(1, type.getCount());
    assertEquals(duration, type.getTotalDuration());
    assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
    assertEquals(ILaunchManager.DEBUG_MODE, type.getLaunchModeId());
    assertEquals(config.getName(), type.getName());
    assertEquals(filePaths.size(), type.getFilePath().size());
    for (IPath path : filePaths) {
      assertTrue(type.getFilePath().contains(path.toString()));
    }
  }

//Empty class for testing.
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
}
