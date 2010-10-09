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
package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.ui.internal.util.Pair;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

/**
 * @see LaunchPageLabelProvider
 */
public class LaunchPageLabelProviderTest {

  /*
   * Class to help testing. 
   */
  private static class ValueProvider implements IValueProvider {
    
    private final Map<? extends Object, Long> data;
    
    ValueProvider() {
      this(Collections.<Object, Long>emptyMap());
    }
    
    ValueProvider(Map<? extends Object, Long> data) {
      this.data = checkNotNull(data);
    }
    
    @Override
    public long getMaxValue() {
      if (data.isEmpty()) {
        return 0;
      }
      return Collections.max(data.values());
    }

    @Override
    public long getValue(Object element) {
      Long val = data.get(element);
      if (val == null) {
        return 0;
      }
      return val;
    }

    @Override
    public boolean shouldPaint(Object element) {
      return data.containsKey(element);
    }
  }
  
  private static final Shell shell = 
      new Shell(PlatformUI.getWorkbench().getDisplay());
  
  private LaunchPageLabelProvider labels;
  private DateLabelProvider dates;
  
  private static LocalDate date;
  private static ILaunchConfigurationType configType;
  private static ILaunchMode launchMode;
  private static Pair<String, String> launch;
  private static IProject project;
  private static IFolder folder;
  private static IFile file;
  
  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }
  
  @BeforeClass
  public static void beforeClass() {
    project = ResourcesPlugin.getWorkspace().getRoot().getProject("p");
    folder = project.getFolder("folder");
    file = project.getFile("file.txt");
    date = new LocalDate();
    ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
    configType = manager.getLaunchConfigurationTypes()[0];
    launchMode = manager.getLaunchMode(ILaunchManager.RUN_MODE);
    launch = Pair.create("launchName", configType.getIdentifier());
  }
  
  @After
  public void after() {
    dates.dispose();
    labels.dispose();
  }

  @Before
  public void before() {
    labels = create(new ValueProvider(), new ValueProvider());
    dates = new DateLabelProvider();
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_arg1IsNull() {
    create(null, new ValueProvider());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_arg2IsNull() {
    create(new ValueProvider(), null);
  }
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_argsAreNull() {
    create(null, null);
  }
  
  @Test
  public void testGetColumnImage_0_argIsFile() {
    assertThat(labels.getColumnImage(file, 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsFileInTreeNode() {
    assertThat(labels.getColumnImage(new TreeNode(file), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsFolder() {
    assertThat(labels.getColumnImage(folder, 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsFolderInTreeNode() {
    assertThat(labels.getColumnImage(new TreeNode(folder), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunch() {
    assertThat(labels.getColumnImage(launch, 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunchConfigType() {
    assertThat(
        labels.getColumnImage(getDefinedLaunchType(), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunchConfigTypeInTreeNode() {
    assertThat(
        labels.getColumnImage(new TreeNode(getDefinedLaunchType()), 0), 
        notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunchInTreeNode() {
    assertThat(labels.getColumnImage(new TreeNode(launch), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunchMode() {
    assertThat(labels.getColumnImage(getDefinedMode(), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLaunchModeInTreeNode() {
    assertThat(
        labels.getColumnImage(new TreeNode(getDefinedMode()), 0), 
        notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLocalDate() {
    assertThat(labels.getColumnImage(date, 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsLocalDateInTreeNode() {
    assertThat(labels.getColumnImage(new TreeNode(date), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsProject() {
    assertThat(labels.getColumnImage(project, 0), notNullValue());
  }
  
  @Test
  public void testGetColumnImage_0_argIsProjectInTreeNode() {
    assertThat(labels.getColumnImage(new TreeNode(project), 0), notNullValue());
  }
  
  @Test
  public void testGetColumnText_0_argIsFile() {
    assertEquals(file.getName(), labels.getColumnText(file, 0));
  }
  
  @Test
  public void testGetColumnText_0_argIsFileInTreeNode() {
    assertEquals(file.getName(), labels.getColumnText(new TreeNode(file), 0));
  }
  
  @Test
  public void testGetColumnText_0_argIsFolder() {
    assertEquals(folder.getName(), labels.getColumnText(folder, 0));
  }
  
  @Test
  public void testGetColumnText_0_argIsFolderInTreeNode() {
    assertEquals(
        folder.getName(), labels.getColumnText(new TreeNode(folder), 0));
  }

  @Test
  public void testGetColumnText_0_argIsLocalDate() {
    DateLabelProvider dates = new DateLabelProvider();
    try {
      assertEquals(dates.getText(date), labels.getColumnText(date, 0));
    } finally {
      dates.dispose();
    }
  }
  
  @Test
  public void testGetColumnText_0_argIsLocalDateInTreeNode() {
    assertEquals(
        dates.getText(date), labels.getColumnText(new TreeNode(date), 0));
  }
  
  @Test
  public void testGetColumnText_0_argIsProject() {
    assertEquals(project.getName(), labels.getColumnText(project, 0));
  }
  
  @Test
  public void testGetColumnText_0_argsIsProjectInTreeNode() {
    assertEquals(
        project.getName(), labels.getColumnText(new TreeNode(project), 0));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunch_returnsNonnull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    testGetColumnText_1(launch);
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunch_returnsNull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    assertNull(labels.getColumnText(launch, 1));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchInTreeNode_returnsNonnull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    testGetColumnText_1(new TreeNode(launch));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchInTreeNode_returnsNull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    assertThat(labels.getColumnText(new TreeNode(launch), 1), nullValue());
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchMode_returnsNull() {
    assertThat(labels.getColumnText(getDefinedMode(), 1), nullValue());
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchModeIn_returnsNonnull() {
    testGetColumnText_1(getDefinedMode());
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchModeInTreeNode_returnsNonnull() {
    testGetColumnText_1(new TreeNode(getDefinedMode()));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchModeInTreeNode_returnsNull() {
    assertThat(
        labels.getColumnText(new TreeNode(getDefinedMode()), 1), 
        nullValue());
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchType_returnsNonnull() {
    testGetColumnText_1(getDefinedLaunchType());
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchType_returnsNull() {
    assertNull(labels.getColumnText(getDefinedLaunchType(), 1));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchTypeInTreeNode_returnsNonnull() {
    testGetColumnText_1(new TreeNode(getDefinedLaunchType()));
  }
  
  @Test
  public void testGetColumnText_1_argIsLaunchTypeInTreeNode_returnsNull() {
    assertNull(labels.getColumnText(new TreeNode(getDefinedLaunchType()), 1));
  }
  
  @Test
  public void testGetColumnText_1_argIsLocalDate_returnsNonnull() {
    testGetColumnText_1(new LocalDate());
  }
  
  @Test
  public void testGetColumnText_1_argIsLocalDate_returnsNull() {
    assertNull(labels.getColumnText(new LocalDate(), 1));
  }
  
  @Test
  public void testGetColumnText_1_argIsLocalDateInTreeNode_returnsNonnull() {
    testGetColumnText_1(new TreeNode(new LocalDate()));
  }
  
  @Test
  public void testGetColumnText_1_argIsLocalDateInTreeNode_returnsNull() {
    assertNull(labels.getColumnText(new TreeNode(new LocalDate()), 1));
  }
  
  @Test
  public void testGetColumnText_2_allReturnsNull() {
    assertNull(labels.getColumnText(new TreeNode(date), 2));
    assertNull(labels.getColumnText(new TreeNode(launchMode), 2));
    assertNull(labels.getColumnText(new TreeNode(configType), 2));
    assertNull(labels.getColumnText(new TreeNode(launch), 2));

    assertNull(labels.getColumnText(date, 2));
    assertNull(labels.getColumnText(launchMode, 2));
    assertNull(labels.getColumnText(launch, 2));
    assertNull(labels.getColumnText(configType, 2));
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunch_returnsNonnull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    testGetColumnText_3(launch);
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunch_returnsNull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    assertThat(labels.getColumnText(launch, 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchInTreeNode_returnsNonnull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    testGetColumnText_3(new TreeNode(launch));
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchInTreeNode_returnsNull() {
    Pair<String, String> launch = Pair.create("launchName", "launchTypeId");
    assertThat(labels.getColumnText(new TreeNode(launch), 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchMode_returnsNonnull() {
    testGetColumnText_3(getDefinedMode());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchMode_returnsNull() {
    assertThat(labels.getColumnText(getDefinedMode(), 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchModeInTreeNode_returnsNonnull() {
    testGetColumnText_3(new TreeNode(getDefinedMode()));
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchModeInTreeNode_returnsNull() {
    assertThat(
        labels.getColumnText(new TreeNode(getDefinedMode()), 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchType_returnsNonnull() {
    testGetColumnText_3(getDefinedLaunchType());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchType_returnsNull() {
    assertThat(labels.getColumnText(getDefinedLaunchType(), 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchTypeInTreeNode_returnsNonnull() {
    testGetColumnText_3(new TreeNode(getDefinedLaunchType()));
  }
  
  @Test
  public void testGetColumnText_3_argIsLaunchTypeInTreeNode_returnsNull() {
    assertThat(
        labels.getColumnText(new TreeNode(getDefinedLaunchType()), 3), 
        nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLocalDate_returnsNonnull() {
    testGetColumnText_3(new LocalDate());
  }
  
  @Test
  public void testGetColumnText_3_argIsLocalDate_returnsNull() {
    assertThat(labels.getColumnText(new LocalDate(), 3), nullValue());
  }
  
  @Test
  public void testGetColumnText_3_argIsLocalDateInTreeNode_returnsNonnull() {
    testGetColumnText_3(new TreeNode(new LocalDate()));
  }
  
  @Test
  public void testGetColumnText_3_argIsLocalDateInTreeNode_returnsNull() {
    assertThat(
        labels.getColumnText(new TreeNode(new LocalDate()), 3), nullValue());
  }
  
  @Test
  public void testGetForeground_argIsLaunchConfigTypeInTreeNodeUndefined() {
    assertNotNull(labels.getForeground(
        new TreeNode(new UndefinedLaunchConfigurationType(""))));
  }
  
  @Test
  public void testGetForeground_argIsLaunchConfigTypeUndefined() {
    assertNotNull(
        labels.getForeground(new UndefinedLaunchConfigurationType("")));
  }
  
  @Test
  public void testGetForeground_argIsLaunchModeInTreeNodeUndefined() {
    assertNotNull(labels.getForeground(
        new TreeNode(new UndefinedLaunchMode(""))));
  }
  
  @Test
  public void testGetForeground_argIsLaunchModeUndefined() throws Exception {
    assertNotNull(labels.getForeground(new UndefinedLaunchMode("")));
  }
  
  @Test
  public void testGetForeground_argIsResourceExists() throws Exception {
    IProject proj = project.getWorkspace().getRoot()
        .getProject(System.nanoTime() + "");
    proj.create(null);
    assertNull(labels.getForeground(proj));
  }
  

  @Test
  public void testGetForeground_argIsResourceInTreeNodeExists() throws Exception {
    IProject proj = project.getWorkspace().getRoot()
        .getProject(System.nanoTime() + "");
    proj.create(null);
    assertNull(labels.getForeground(new TreeNode(proj)));
  }
  
  @Test
  public void testGetForeground_argIsResourceInTreeNodeNotExists() throws Exception {
    IProject proj = 
        project.getWorkspace().getRoot().getProject(System.nanoTime() + "");
    assertNotNull(labels.getForeground(new TreeNode(proj)));
  }
  

  @Test
  public void testGetForeground_argIsResourceNotExists() throws Exception {
    IProject proj = 
        project.getWorkspace().getRoot().getProject(System.nanoTime() + "");
    assertNotNull(labels.getForeground(proj));
  }
  
  @Test
  public void testGetImage_argIsFile() {
    assertThat(labels.getImage(file), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsFileInTreeNode() {
    assertThat(labels.getImage(new TreeNode(file)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsFolder() {
    assertThat(labels.getImage(folder), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsFolderInTreeNode() {
    assertThat(labels.getImage(new TreeNode(folder)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunch() {
    assertThat(labels.getImage(launch), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunchConfigType() {
    assertThat(labels.getImage(configType), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunchConfigTypeInTreeNode() {
    assertThat(labels.getImage(new TreeNode(configType)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunchInTreeNode() {
    assertThat(labels.getImage(new TreeNode(launch)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunchMode() {
    assertThat(labels.getImage(launchMode), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLaunchModeInTreeNode() {
    assertThat(labels.getImage(new TreeNode(launchMode)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLocaDate() {
    assertThat(labels.getImage(date), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsLocalDateInTreeNode() {
    assertThat(labels.getImage(new TreeNode(date)), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsProject() {
    assertThat(labels.getImage(project), notNullValue());
  }
  
  @Test
  public void testGetImage_argIsProjectInTreeNode() {
    assertThat(labels.getImage(new TreeNode(project)), notNullValue());
  }
  
  @Test
  public void testGetText_argIsFile() {
    assertThat(labels.getText(file), equalTo(file.getName()));
  }
  
  @Test
  public void testGetText_argIsFileTreeNode() {
    assertThat(labels.getText(new TreeNode(file)), equalTo(file.getName()));
  }
  
  @Test
  public void testGetText_argIsFolder() {
    assertThat(labels.getText(folder), equalTo(folder.getName()));
  }
  
  @Test
  public void testGetText_argIsFolderInTreeNode() {
    assertThat(labels.getText(new TreeNode(folder)), equalTo(folder.getName()));
  }
  
  @Test
  public void testGetText_argIsLaunch() {
    assertThat(labels.getText(launch), equalTo(launch.getFirst()));
  }
  
  @Test
  public void testGetText_argIsLaunchConfigType() {
    assertThat(labels.getText(configType), equalTo(configType.getName()));
  }
  
  @Test
  public void testGetText_argIsLaunchConfigTypeTreeNode() {
    assertThat(
        labels.getText(new TreeNode(configType)), 
        equalTo(configType.getName()));
  }
  
  @Test
  public void testGetText_argIsLaunchMode() {
    assertThat(
        labels.getText(launchMode), 
        equalTo(launchMode.getLabel().replace("&", "")));
  }
  
  @Test
  public void testGetText_argIsLaunchModeTreeNode() {
    assertThat(
        labels.getText(new TreeNode(launchMode)), 
        equalTo(launchMode.getLabel().replace("&", "")));
  }
  
  @Test
  public void testGetText_argIsLaunchTreeNode() {
    assertThat(
        labels.getText(new TreeNode(launch)), 
        equalTo(launch.getFirst()));
  }
  
  @Test
  public void testGetText_argIsLocalDate() {
    DateLabelProvider provider = new DateLabelProvider();
    try {
      assertThat(labels.getText(date), equalTo(provider.getText(date)));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testGetText_argIsLocalDateInTreeNode() {
    DateLabelProvider provider = new DateLabelProvider();
    try {
      assertThat(
          labels.getText(new TreeNode(date)), equalTo(provider.getText(date)));
    } finally {
      provider.dispose();
    }
  }
  
  @Test
  public void testGetText_argIsProject() {
    assertThat(labels.getText(project), equalTo(project.getName()));
  }
  
  @Test
  public void testGetText_argIsProjectInTreeNode() {
    assertThat(labels.getText(new TreeNode(project)), equalTo(project.getName()));
  }
  
  protected LaunchPageLabelProvider create(IValueProvider counts, IValueProvider durations) {
    return new LaunchPageLabelProvider(counts, durations);
  }

  /**
   * @return A defined ILaunchConfigurationType.
   */
  private ILaunchConfigurationType getDefinedLaunchType() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationTypes()[0];
  }

  /**
   * @return A defined ILaunchMode.
   */
  private ILaunchMode getDefinedMode() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchMode(ILaunchManager.DEBUG_MODE);
  }

  /**
   * Test helper method for testing getColumnText(Object, 1).
   * @param obj The object to be tested.
   */
  private void testGetColumnText_1(Object obj) {
    long count = 2874546;
    LaunchPageLabelProvider provider = create(
        new ValueProvider(ImmutableMap.of(obj, count)), new ValueProvider());
    try {
      assertThat(provider.getColumnText(obj, 1), equalTo(count + ""));
    } finally {
      provider.dispose();
    }
  }

  /**
   * Test helper method for testing getColumnText(Object, 3).
   * @param obj The object to be tested.
   */
  private void testGetColumnText_3(Object obj) {
    long millis = 2874546;
    LaunchPageLabelProvider provider = create(
        new ValueProvider(), new ValueProvider(ImmutableMap.of(obj, millis)));
    try {
      assertThat(provider.getColumnText(obj, 3), equalTo(format(millis)));
    } finally {
      provider.dispose();
    }
  }
}
