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

import rabbit.data.IFileStore;
import rabbit.data.access.model.ZLaunchDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.pages.LaunchPageContentProvider;
import rabbit.ui.internal.util.LaunchResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @see LaunchPageContentProvider
 */
public class LaunchPageContentProviderTest {

  private LaunchPageContentProvider provider;
  private IFileStore fileMapper = DataHandler.getFileMapper();
  private IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

  @Before
  public void before() {
    provider = new LaunchPageContentProvider();
  }

  @Test
  public void testGetChildren() {
    ZLaunchDescriptor des = new ZLaunchDescriptor();

    IFile file = root.getProject("p").getFolder("f").getFile("ff");
    String fileId = fileMapper.insert(file);
    des.getFileIds().addAll(new HashSet<String>(Arrays.asList(fileId)));
    provider.inputChanged(null, null, new HashSet<ZLaunchDescriptor>(Arrays
        .asList(des)));

    LaunchResource projectRes = new LaunchResource(des, file.getProject());
    LaunchResource folderRes = new LaunchResource(des, file.getParent());

    assertEquals(projectRes, provider.getChildren(des)[0]);
    assertEquals(folderRes, provider.getChildren(projectRes)[0]);
    assertEquals(file, provider.getChildren(folderRes)[0]);
  }

  @Test
  public void testGetElements() {
    assertNotNull(provider.getElements(new HashSet<Object>()));
    assertEquals(2, provider.getElements(new HashSet<String>(Arrays.asList("1",
        "2"))).length);
  }

  @Test
  public void testHasChildren() {
    ZLaunchDescriptor des = new ZLaunchDescriptor();
    provider.inputChanged(null, null, new HashSet<ZLaunchDescriptor>(Arrays
        .asList(des)));
    assertFalse(provider.hasChildren(des));

    IFile file = root.getProject("p").getFolder("f").getFile("ff");
    String fileId = fileMapper.insert(file);
    des.getFileIds().addAll(new HashSet<String>(Arrays.asList(fileId)));
    provider.inputChanged(null, null, new HashSet<ZLaunchDescriptor>(Arrays
        .asList(des)));

    assertTrue(provider.hasChildren(des));
    assertTrue(provider.hasChildren(new LaunchResource(des, file.getProject())));
    assertTrue(provider.hasChildren(new LaunchResource(des, file.getParent())));
    assertFalse(provider.hasChildren(new LaunchResource(des, file)));
    assertFalse(provider.hasChildren(file));
  }

  @Test
  public void testInputChanged() {
    ZLaunchDescriptor des = new ZLaunchDescriptor();
    provider.inputChanged(null, null, new HashSet<ZLaunchDescriptor>(Arrays
        .asList(des)));
    assertFalse(provider.hasChildren(des));

    IFile file = root.getProject("p").getFolder("f").getFile("ff");
    String fileId = fileMapper.insert(file);
    des.getFileIds().addAll(new HashSet<String>(Arrays.asList(fileId)));
    provider.inputChanged(null, null, new HashSet<ZLaunchDescriptor>(Arrays
        .asList(des)));

    assertTrue(provider.hasChildren(des));

    provider.inputChanged(null, null, null);
    assertFalse(provider.hasChildren(des));
  }

}
