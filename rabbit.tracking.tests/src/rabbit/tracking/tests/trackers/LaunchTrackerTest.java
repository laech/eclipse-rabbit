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
package rabbit.tracking.tests.trackers;

import rabbit.data.store.model.LaunchEvent;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.LaunchTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.internal.registry.ConfigurationElement;
import org.eclipse.core.internal.registry.ConfigurationElementHandle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationType;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @see LaunchTracker
 */
@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public class LaunchTrackerTest extends AbstractTrackerTest<LaunchEvent> {
  
  /* TODO
   * This is not a completed test class, need to test launches in debug mode and
   * hitting breakpoints to record files, current manual testing is done on
   * this.
   */

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
    public String getName() {
      return "MyType";
    }
  }

  private static SWTWorkbenchBot bot = new SWTWorkbenchBot();

  private LaunchTracker tracker;

  @Before
  public void before() {
    tracker = new LaunchTracker();
  }
  
  @Test
  public void testObserverIsAdded() {
    tracker.setEnabled(false); // It should remove itself from the observable
    int count = TrackingPlugin.getDefault().getIdleDetector().countObservers();
    tracker.setEnabled(true); // It should add itself to the observable
    assertEquals(count + 1, TrackingPlugin.getDefault().getIdleDetector().countObservers());
  }

  /*
   * Tests the launching in normal mode ("run");
   */
  @Test
  public void testRun() throws CoreException, IOException, InterruptedException {
    bot.viewByTitle("Welcome").close();

    final String projectName = "Enfo";
    final String className = "name";
    long duration = 2000;

    // Create a new Java project:
    bot.menu("File").menu("New").menu("Project...").click();
    bot.tree().expandNode("Java").select("Java Project");
    bot.button("Next >").click();
    bot.textWithLabel("Project name:").setText(projectName);
    bot.button("Finish").click();

    try {
      // Answer yes to open the Java perspective:
      bot.button("Yes").click();
    } catch (WidgetNotFoundException e) {

    }

    // Create a new Java class:
    bot.menu("File").menu("New").menu("Class").click();
    bot.textWithLabel("Source folder:").setText(projectName + "/src");
    bot.textWithLabel("Name:").setText(className);
    bot.button("Finish").click();

    // Open the file:
    bot.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
            new Path("/" + projectName + "/src/" + className + ".java"));
        try {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .openEditor(new FileEditorInput(file),
                  "org.eclipse.jdt.ui.CompilationUnitEditor", true);
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    });

    // Write a main method to the class, set the thread sleep time:
    SWTBotEclipseEditor editor = bot.activeEditor().toTextEditor();
    editor.setText("public class " + className + " {"
        + "public static void main(String[] args) {" + "try { Thread.sleep("
        + duration + "); }" + "catch (InterruptedException e) {}" + "}" + "}");
    editor.save();

    tracker.setEnabled(true);
    // Launch the application:
    bot.menu("Run").menu("Run As").menu("1 Java Application").click();
    // Wait for the launch to finish:
    Thread.sleep(duration * 2);
    tracker.setEnabled(false);

    // Check the result:
    assertEquals(1, tracker.getData().size());
    LaunchEvent event = tracker.getData().iterator().next();
    System.out.println(event.getDuration());
    assertTrue(duration - 200 <= event.getDuration());
    assertTrue(duration + 200 >= event.getDuration());
  }

  @Override
  protected LaunchEvent createEvent() {
    ILaunch launch = new Launch(new LaunchConfigurationForTest(),
        ILaunchManager.RUN_MODE, null);
    return new LaunchEvent(new DateTime(), 10, launch, launch
        .getLaunchConfiguration(), new HashSet<String>(Arrays.asList("1", "2")));
  }

  @Override
  protected LaunchTracker createTracker() {
    return new LaunchTracker();
  }
}
