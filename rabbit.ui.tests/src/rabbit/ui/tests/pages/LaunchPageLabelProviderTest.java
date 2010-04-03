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
package rabbit.ui.tests.pages;

import rabbit.data.access.model.LaunchDescriptor;
import rabbit.ui.internal.pages.LaunchPageLabelProvider;
import rabbit.ui.internal.util.LaunchResource;
import rabbit.ui.internal.util.MillisConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @see LaunchPageLabelProvider
 */
public class LaunchPageLabelProviderTest {

  private static LaunchPageLabelProvider provider = new LaunchPageLabelProvider();
  private static IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

  @AfterClass
  public static void afterClass() {
    provider.dispose();
  }

  @Test
  public void testGetColumnImage_0_file() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p")
        .getFile("ff"));
    assertNotNull(provider.getColumnImage(resource, 0));
  }

  @Test
  public void testGetColumnImage_0_folder() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p")
        .getFolder("f"));
    assertNotNull(provider.getColumnImage(resource, 0));
  }

  @Test
  public void testGetColumnImage_0_launchDescriptor() {
    String id = "org.eclipse.jdt.launching.localJavaApplication";
    LaunchDescriptor des = new LaunchDescriptor();
    des.setLaunchTypeId(id);
    assertEquals(DebugUITools.getImage(id), provider.getColumnImage(des, 0));
  }

  @Test
  public void testGetColumnImage_0_project() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p"));
    assertNotNull(provider.getColumnImage(resource, 0));
  }

  @Test
  public void testGetColumnImage_1() {
    LaunchDescriptor des = new LaunchDescriptor();
    des.setLaunchModeId(ILaunchManager.RUN_MODE);
    assertEquals(DebugUITools.getImage(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN),
        provider.getColumnImage(des, 1));
  }

  @Test
  public void testGetForeground() throws CoreException {
    IProject project = root.getProject(System.nanoTime() + "");
    assertNotNull(
        "Should show different color for resources that are not exist",
        provider.getForeground(project));

    project.create(null);
    assertNull(provider.getForeground(project));
  }

  @Test
  public void testGetText_0_file() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p")
        .getFile("fff"));
    assertEquals(resource.getResource().getName(), provider.getColumnText(
        resource, 0));
  }

  @Test
  public void testGetText_0_folder() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p")
        .getFolder("f"));
    assertEquals(resource.getResource().getProjectRelativePath().toString(),
        provider.getColumnText(resource, 0));
  }

  @Test
  public void testGetText_0_launchDescriptor() {
    String name = "aName";
    LaunchDescriptor des = new LaunchDescriptor();
    des.setLaunchName(name);
    assertEquals(name, provider.getColumnText(des, 0));
  }

  @Test
  public void testGetText_0_project() {
    LaunchDescriptor des = new LaunchDescriptor();
    LaunchResource resource = new LaunchResource(des, root.getProject("p"));
    assertEquals(resource.getResource().getName(), provider.getColumnText(
        resource, 0));
  }

  @Test
  public void testGetText_1() {
    LaunchDescriptor des = new LaunchDescriptor();
    des.setLaunchModeId(ILaunchManager.RUN_MODE);
    assertEquals(ILaunchManager.RUN_MODE, provider.getColumnText(des, 1));
  }

  @Test
  public void testGetText_2() {
    LaunchDescriptor des = new LaunchDescriptor();
    des.setCount(2);
    assertEquals(des.getCount() + "", provider.getColumnText(des, 2));
  }

  @Test
  public void testGetText_4() {
    long duration = 3493872;
    LaunchDescriptor des = new LaunchDescriptor();
    des.setTotalDuration(duration);
    assertEquals(MillisConverter.toDefaultString(duration), provider
        .getColumnText(des, 4));
  }
}
