package rabbit.data.test.xml.convert;

import rabbit.data.internal.xml.convert.LaunchEventConverter;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.internal.registry.ConfigurationElement;
import org.eclipse.core.internal.registry.ConfigurationElementHandle;
import org.eclipse.core.runtime.CoreException;
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
    Set<String> fileIds = new HashSet<String>();
    fileIds.add("abc");
    fileIds.add("def");

    LaunchEvent event = new LaunchEvent(time, duration, launch, config, fileIds);
    LaunchEventType type = converter.convert(event);

    assertEquals(1, type.getCount());
    assertEquals(duration, type.getTotalDuration());
    assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
    assertEquals(ILaunchManager.DEBUG_MODE, type.getLaunchModeId());
    assertEquals(config.getName(), type.getName());
    assertEquals(fileIds.size(), type.getFileId().size());
    fileIds.removeAll(type.getFileId());
    assertTrue(fileIds.isEmpty());
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
